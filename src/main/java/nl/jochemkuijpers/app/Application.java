package nl.jochemkuijpers.app;

import nl.jochemkuijpers.app.scenes.MaterialTestScene;
import nl.jochemkuijpers.app.scenes.MazeScene;
import nl.jochemkuijpers.app.scenes.StackedShapeScene;
import nl.jochemkuijpers.app.window.InputCapturer;
import nl.jochemkuijpers.app.window.ApplicationEvent;
import nl.jochemkuijpers.app.window.Window;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Camera;
import nl.jochemkuijpers.raytrace.Scene;
import nl.jochemkuijpers.raytrace.renderer.FrameBuffer;
import nl.jochemkuijpers.raytrace.renderer.RenderTile;
import nl.jochemkuijpers.workerpool.WorkerPool;

import java.awt.event.*;
import java.util.*;

public class Application {
    private Window window;
    private FrameBuffer frameBuffer;
    private List<RenderTile> renderTiles;

    private final WorkerPool workerPool;
    private final Deque<ApplicationEvent> applicationEvents;

    private Camera camera;
    private CameraController cameraController;
    private Scene[] scenes;
    private int sceneIndex = 0;

    private int qualityIndex = 0;
    private int[] qualities = { 0, 1, 4 };
    private boolean needsResize = false;

    private boolean running;

    public Application() {
        applicationEvents = new LinkedList<>();

        // assumption is that hyper threading is enabled; floating point instructions don't benefit from that
        // and since we're constrained by floating point computation, SMT will slow things down, so we only use
        // half of the logical processors available.
        workerPool = new WorkerPool(Runtime.getRuntime().availableProcessors() / 2);
    }

    public static void main(String[] args) {
        Application app = new Application();
        app.start();
    }

    private void start() {
        init();
        loop();
        exit();
    }

    private void init() {
        final int width = 1280;
        final int height = 720;
        window = new Window(width, height);
        frameBuffer = window.getFrameBuffer();

        camera = new Camera(new Vector3(), new Vector3(), Vector3.YIDENT, 90);
        cameraController = new CameraController(camera, 25, new Vector3(0, -2, 0));
        cameraController.setInitialCamera();

        sceneIndex = 0;
        scenes = new Scene[] {
                new StackedShapeScene(),
                new MazeScene(),
                new MaterialTestScene()
        };

        InputCapturer inputCapturer = new InputCapturer(applicationEvents);
        window.addKeyListener(inputCapturer);
        window.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (e.getComponent().getWidth() != frameBuffer.getFrameWidth() ||
                        e.getComponent().getHeight() != frameBuffer.getFrameHeight()) {
                    needsResize = true;
                }
            }
            @Override
            public void componentMoved(ComponentEvent e) { }
            @Override
            public void componentShown(ComponentEvent e) { }
            @Override
            public void componentHidden(ComponentEvent e) { }
        });
    }

    private void loop() {
        running = true;

        boolean mustReset;
        while (running) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
                break;
            }

            window.repaint();

            mustReset = handleEvents();
            ensureRenderJobs(mustReset);
        }
    }

    private boolean handleEvents() {
        boolean mustReset = false;
        if (needsResize) {
            needsResize = false;
            window.resizeFrameBuffer();
            mustReset = true;
        }

        synchronized (applicationEvents) {
            while (applicationEvents.size() > 0) {
                ApplicationEvent event = applicationEvents.pop();

                int camx = 0;
                int camy = 0;

                switch (event.getType()) {
                    case EXIT:
                        running = false;
                        break;
                    case RIGHT:
                        camx += 1;
                        break;
                    case LEFT:
                        camx -= 1;
                        break;
                    case UP:
                        camy += 1;
                        break;
                    case DOWN:
                        camy -= 1;
                        break;
                    case PREV_SCENE:
                        sceneIndex = (sceneIndex + scenes.length - 1) % scenes.length;
                        mustReset = true;
                        break;
                    case NEXT_SCENE:
                        sceneIndex = (sceneIndex + scenes.length + 1) % scenes.length;
                        mustReset = true;
                        break;
                }

                if (camx != 0 || camy != 0) {
                    cameraController.move(camx, camy);
                    mustReset = true;
                }
            }
        }
        return mustReset;
    }

    private void ensureRenderJobs(boolean reset) {
        if (reset || renderTiles == null) {
            workerPool.clearPendingJobs();
            workerPool.interruptCurrentJobs();
            qualityIndex = 0;
            renderTiles = camera.getRenderTiles(frameBuffer);
        }

        // don't add new work before the workerpool is done, otherwise they might finish out of order
        if (workerPool.hasWork() || qualityIndex >= qualities.length ) {
            return;
        }

        for (RenderTile tile : renderTiles) {
            final int quality = qualities[qualityIndex];
            final Scene scene = scenes[sceneIndex];
            workerPool.submit(() -> {
                tile.render(scene, quality);
            });
        }
        qualityIndex++;
    }

    private void exit() {
        window.dispose();
        workerPool.shutdownNow();
    }

}

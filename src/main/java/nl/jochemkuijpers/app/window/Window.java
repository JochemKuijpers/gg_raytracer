package nl.jochemkuijpers.app.window;

import nl.jochemkuijpers.raytrace.renderer.FrameBuffer;

import javax.swing.*;

public class Window extends JFrame {
    private final JPanelFrameBuffer frameBuffer;

    public Window(int width, int height) {
        super("Interactive Raytrace Renderer");

        this.frameBuffer = new JPanelFrameBuffer(width, height);
        add(frameBuffer);

        setSize(width, height);
        setLocationByPlatform(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    public FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public void resizeFrameBuffer() {
        frameBuffer.resizeFrameBuffer(getWidth(), getHeight());
    }
}

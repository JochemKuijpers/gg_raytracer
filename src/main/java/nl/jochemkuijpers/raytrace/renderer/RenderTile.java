package nl.jochemkuijpers.raytrace.renderer;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;

import java.util.Arrays;

/**
 * A render tile is a subsection of the frame being rendered. The render tile has all the information necessary
 * to compute the outgoing rays and convert the result to RGB pixel values, which it then writes to the frame buffer.
 *
 * Typically a render tile is rendered by an asynchronous worker.
 */
@SuppressWarnings("Duplicates")
public class RenderTile {

    private final Vector3 position;
    private final Vector3 gaze;
    private final Vector3 horz;
    private final Vector3 vert;
    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;
    private final float vertFOV;

    private final int targetX;
    private final int targetY;
    private final int targetWidth;
    private final int targetHeight;

    private final FrameBuffer target;
    private final int[] buffer;

    public RenderTile(
            Vector3 position, Vector3 gaze, Vector3 horz, Vector3 vert,
            float xMin, float xMax, float yMin, float yMax, float vertFOV,
            int targetX, int targetY, int targetWidth, int targetHeight, FrameBuffer target
    ) {
        this.position = position;
        this.gaze = gaze;
        this.horz = horz;
        this.vert = vert;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.vertFOV = vertFOV;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.target = target;
        this.buffer = new int[targetWidth * targetHeight];
        Arrays.fill(buffer, 0, buffer.length - 1, 0xFF000000);
    }

    /**
     * Render the current tile and write it to the framebuffer when done.
     *
     * - A factor of 0 means that every 16×16 target pixels gets one ray and is filled with this color
     * - A factor of 1 means that every frame buffer pixels gets exactly one ray
     * - A factor of > 1 means that every frame buffer pixel gets exactly factor * factor rays; super-sampling
     * - A factor of < 0 is invalid.
     *
     * @param world the world to render
     * @param resFactor resolution factor
     */
    public void render(Scene world, int resFactor) {
        if (resFactor > 0) {
            // visually show this tile is being rendered by drawing outlines
            drawOutlines();
            if (!Thread.interrupted()) {
                target.write(targetX, targetY, targetWidth, targetHeight, buffer);
            }
        }

        switch (resFactor) {
            case 0:
                renderPatched(world, 8);
                break;
            case 1:
                renderOneToOne(world);
                break;
            default:
                if (resFactor < 0) {
                    throw new IllegalArgumentException("negative resFactor not allowed!");
                }
                renderSupersampled(world, resFactor);
                break;
        }
    }

    private void drawOutlines() {
        for (int y = 0; y < targetHeight; y++) {
            buffer[y * targetWidth] = 0xFFFF0000;
            buffer[(y+1) * targetWidth - 1] = 0xFFFF0000;
        }

        for (int x = 0; x < targetWidth; x++) {
            buffer[x] = 0xFFFF0000;
            buffer[targetWidth * targetHeight - x - 1] = 0xFFFF0000;
        }
    }

    /** Renders the tile in patchSize × patchSize patches, one ray through the middle of each patch. */
    private void renderPatched(Scene world, int patchSize) {
        Ray ray = new Ray(0, position, Vector3.ZERO);
        Vector3 heading = ray.getHeading();
        Color color = new Color();

        float npSize = (float) Math.sin(vertFOV / 2);
        float npDistance = (float) Math.cos(vertFOV / 2);

        float ty, tx;
        for (int y = 0; y < targetHeight; y += patchSize) {
            ty = (y + 0.5f * patchSize) / (targetHeight + 1);
            for (int x = 0; x < targetWidth; x += patchSize) {
                tx = (x + 0.5f * patchSize) / (targetWidth + 1);

                Vector3.addMul(Vector3.ZERO, gaze, npDistance, heading);
                Vector3.addMul(heading, horz, (xMin * (1 - tx) + xMax * tx) * npSize, heading);
                Vector3.addMul(heading, vert, (yMin * (1 - ty) + yMax * ty) * npSize, heading);
                Vector3.normalize(heading, heading);

                color.set(0, 0, 0);
                world.query(ray, color);

                int bufferColor = Color.gammaEncode(color, x, y);
                if (patchSize == 1) {
                    buffer[y * targetWidth + x] = bufferColor;
                } else {
                    for (int j = 0; j < patchSize; j++) {
                        if (y + j >= targetHeight) { break; }
                        for (int i = 0; i < patchSize; i++) {
                            if (x + i >= targetWidth) { break; }
                            buffer[(y + j) * targetWidth + (x + i)] = bufferColor;
                        }
                    }
                }
            }
        }
        if (!Thread.interrupted()) {
            target.write(targetX, targetY, targetWidth, targetHeight, buffer);
        }
    }

    private void renderOneToOne(Scene world) {
        Ray ray = new Ray(0, position, Vector3.ZERO);
        Vector3 heading = ray.getHeading();
        Color color = new Color();

        float npSize = (float) Math.sin(vertFOV / 2);
        float npDistance = (float) Math.cos(vertFOV / 2);

        float ty, tx;
        for (int y = 0; y < targetHeight; y++) {
            ty = (y + 0.5f) / (targetHeight + 1);
            for (int x = 0; x < targetWidth; x++) {
                tx = (x + 0.5f) / (targetWidth + 1);

                Vector3.addMul(Vector3.ZERO, gaze, npDistance, heading);
                Vector3.addMul(heading, horz, (xMin * (1 - tx) + xMax * tx) * npSize, heading);
                Vector3.addMul(heading, vert, (yMin * (1 - ty) + yMax * ty) * npSize, heading);
                Vector3.normalize(heading, heading);

                color.set(0, 0, 0);
                world.query(ray, color);
                buffer[y * targetWidth + x] = Color.gammaEncode(color, x, y);
            }
        }
        if (!Thread.interrupted()) {
            target.write(targetX, targetY, targetWidth, targetHeight, buffer);
        }
    }

    private void renderSupersampled(Scene world, int msaa) {
        Ray ray = new Ray(0, position, Vector3.ZERO);
        Vector3 heading = ray.getHeading();
        Color color = new Color();
        Color sample = new Color();
        float sampleWeight = 1f / (msaa * msaa);

        float npSize = (float) Math.sin(vertFOV / 2);
        float npDistance = (float) Math.cos(vertFOV / 2);

        float ty, tx;
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                if (Thread.interrupted()) { return; }
                color.set(0, 0, 0);

                for (int mj = 0; mj < msaa; mj++) {
                    ty = (y + (0.5f + mj) / msaa) / (targetHeight + 1);
                    for (int mi = 0; mi < msaa; mi++) {
                        tx = (x + (0.5f + mi) / msaa) / (targetWidth + 1);

                        Vector3.addMul(Vector3.ZERO, gaze, npDistance, heading);
                        Vector3.addMul(heading, horz, (xMin * (1 - tx) + xMax * tx) * npSize, heading);
                        Vector3.addMul(heading, vert, (yMin * (1 - ty) + yMax * ty) * npSize, heading);
                        Vector3.normalize(heading, heading);

                        sample.set(0, 0, 0);
                        world.query(ray, sample);

                        Vector3.addMul(color, sample, sampleWeight, color);
                    }
                }

                buffer[y * targetWidth + x] = Color.gammaEncode(color, x, y);
            }
        }
        if (!Thread.interrupted()) {
            target.write(targetX, targetY, targetWidth, targetHeight, buffer);
        }
    }

    /**
     * @return the priority score of this tile, lower is better. Based on distance to camera center.
     */
    public float getPriority() {
        return Math.max(Math.abs(xMin + xMax), Math.abs(yMin + yMax));
    }
}

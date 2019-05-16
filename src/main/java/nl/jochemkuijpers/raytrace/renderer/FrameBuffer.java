package nl.jochemkuijpers.raytrace.renderer;

public interface FrameBuffer {
    int getFrameWidth();
    int getFrameHeight();

    /**
     * Resize the framebuffer, subsequent calls to write should fall in this boundary.
     * @param width width of the frame buffer
     * @param height height of the frame buffer
     */
    void resizeFrameBuffer(int width, int height);

    /**
     * Writes the data to the frame buffer. Pixel values are assumed ARGB.
     * @param x left-most pixel position of the data buffer
     * @param y top-most pixel position of the data buffer
     * @param w width of the data buffer
     * @param h height of the data buffer
     * @param data the pixel data
     */
    void write(int x, int y, int w, int h, int[] data);
}

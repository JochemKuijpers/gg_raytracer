package nl.jochemkuijpers.app.window;

import nl.jochemkuijpers.raytrace.renderer.FrameBuffer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JPanelFrameBuffer extends JPanel implements FrameBuffer {
    private int frameWidth;
    private int frameHeight;
    private BufferedImage image;
    private JLabel label;

    public JPanelFrameBuffer(int width, int height) {
        frameWidth = width;
        frameHeight = height;
        updateImage();

        label = new JLabel(
                "<html>[ESC] to quit<br>" +
                "[ARROW KEYS] to rotate the camera<br>" +
                "[PAGE UP/DOWN] to change scene<br>" +
                "The red boxes indicate currently rendering tiles<br><br>" +
                "A scene is rendered in three passes:<br>" +
                "low resolution, native resolution and 4×4 super-sampling<br><br>" +
                "(C) Jochem Kuijpers, 2019</html>"
        );
        label.setForeground(Color.WHITE);
        label.setBackground(new Color(0, 0, 0, 32));
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(4, 4, 4, 4));
        add(label);
    }

    private void updateImage() {
        BufferedImage newImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);

        if (image != null) {
            // copy a scaled version of the old image to the new image as a first approximation
            // of what it should look like. The first render pass will soon replace it anyway.
            Image scaled = image.getScaledInstance(frameWidth, frameHeight, Image.SCALE_FAST);
            Graphics g = newImage.getGraphics();
            g.drawImage(scaled, 0, 0, null);
            g.dispose();
        }

        this.image = newImage;
    }

    @Override
    public int getFrameWidth() {
        return frameWidth;
    }

    @Override
    public int getFrameHeight() {
        return frameHeight;
    }

    @Override
    public void resizeFrameBuffer(int width, int height) {
        frameWidth = width;
        frameHeight = height;
        updateImage();
    }

    @Override
    public void write(int x, int y, int w, int h, int[] data) {
        // there's a hard to avoid race-condition where it is possible that a pre-resize tile is trying to write to
        // our post-resize image. Typically this is not a big problem, as resizing the window triggers re-rendering
        // everything so it will be overwritten very soon anyway. However, we must be careful not to draw outside the
        // image boundaries. In cases where invalid writes would take place, we can just simply skip them as they are
        // always invalid writes.

        if (x + w > image.getWidth() || y + h > image.getHeight()) {
            return;
        }

        image.setRGB(x, y, w, h, data, 0, w);
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
        label.paint(g);
    }
}

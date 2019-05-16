package nl.jochemkuijpers.raytrace;

import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.renderer.FrameBuffer;
import nl.jochemkuijpers.raytrace.renderer.RenderTile;

import java.util.ArrayList;
import java.util.List;

/**
 * The raytracing camera. While it does not directly produce rays, it partitions the framebuffer into tiles,
 * each of which is provided with all the necessary information to render that tile. The render tiles are
 * responsible for producing a picture.
 */
public class Camera {
    /** Size of render tiles */
    public static final int RENDER_TILE_SIZE = 64;

    private final Vector3 viewUp;
    private final Vector3 position;

    private final Vector3 viewGaze; // traditionally the -Z in OpenGL clip/view space
    private final Vector3 viewHorz; // traditionally the +X in OpenGL clip/view space
    private final Vector3 viewVert; // traditionally the +Y in OpenGL clip/view space

    private final float verticalFOV;

    /**
     * Create a new camera at a given position, looking in a certain direction, with a certain FOV.
     * @param position camera position
     * @param gaze gaze direction
     * @param verticalFOV in degrees
     */
    public Camera(Vector3 position, Vector3 gaze, Vector3 up, float verticalFOV) {
        this.position    = new Vector3(position);
        this.verticalFOV = (float) (verticalFOV / 180 * Math.PI);

        this.viewUp   = new Vector3(up);
        this.viewGaze = new Vector3(gaze);
        this.viewHorz = new Vector3();
        this.viewVert = new Vector3();

        computeViewVectors();
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getGaze() {
        return viewGaze;
    }

    public void computeViewVectors() {
        Vector3.fastNormalize(viewUp, viewUp);
        Vector3.fastNormalize(viewGaze, viewGaze);

        Vector3.cross(viewGaze, viewUp, viewHorz);
        Vector3.cross(viewGaze, viewHorz, viewVert);

        Vector3.fastNormalize(viewHorz, viewHorz);
        Vector3.fastNormalize(viewVert, viewVert);
    }

    public List<RenderTile> getRenderTiles(FrameBuffer frameBuffer) {
        final int width = frameBuffer.getFrameWidth();
        final int height = frameBuffer.getFrameHeight();
        final float halfWidth = width / 2f;
        final float halfHeight = height / 2f;

        float aspectRatio = ((float) width) / ((float) height);

        List<RenderTile> tiles = new ArrayList<>((width / RENDER_TILE_SIZE + 1) * (height / RENDER_TILE_SIZE + 1));

        int tileWidth, tileHeight;
        for (int tileY = 0; tileY < height; tileY += RENDER_TILE_SIZE) {
            tileHeight = (tileY + RENDER_TILE_SIZE >= height) ? height - tileY : RENDER_TILE_SIZE;
            for (int tileX = 0; tileX < width; tileX += RENDER_TILE_SIZE) {
                tileWidth = (tileX + RENDER_TILE_SIZE >= width) ? width - tileX : RENDER_TILE_SIZE;

                float xMin = aspectRatio * (tileX - halfWidth) / width;
                float xMax = aspectRatio * ((tileX + tileWidth + 1) - halfWidth) / width;
                float yMin = (tileY - halfHeight) / height;
                float yMax = ((tileY + tileHeight + 1) - halfHeight) / height;

                tiles.add(new RenderTile(
                        position, viewGaze, viewHorz, viewVert,
                        xMin, xMax, yMin, yMax, verticalFOV,
                        tileX, tileY, tileWidth, tileHeight, frameBuffer
                ));
            }
        }

        tiles.sort((a, b) -> {
            float diff = a.getPriority() - b.getPriority();
            if (diff == 0) { return 0; }
            return (diff > 0) ? 1 : -1;
        });

        return tiles;
    }

}

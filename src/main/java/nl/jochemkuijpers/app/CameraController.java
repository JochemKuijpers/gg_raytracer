package nl.jochemkuijpers.app;

import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Camera;

public class CameraController {
    private final Camera camera;
    private final float distance;
    private final Vector3 lookAt;

    public CameraController(Camera camera, float distance, Vector3 lookAt) {
        this.camera = camera;
        this.distance = distance;
        this.lookAt = lookAt;
    }

    /**
     * Moves the camera and adjusts view vectors
     * @param x (-1, 0, 1)
     * @param y (-1, 0, 1)
     */
    public void move(int x, int y) {
        Vector3 cameraPosition = camera.getPosition();
        Vector3 cameraGaze = camera.getGaze();

        Vector3 sideVector = new Vector3();
        Vector3 upVector = new Vector3();
        Vector3.cross(cameraGaze, Vector3.YIDENT, sideVector);
        Vector3.cross(sideVector, cameraGaze, upVector);

        Vector3.addMul(cameraPosition, sideVector, x, cameraPosition);
        Vector3.addMul(cameraPosition, upVector, y, cameraPosition);
        Vector3.mul(cameraPosition, distance / cameraPosition.mag(), cameraPosition);

        Vector3.sub(lookAt, cameraPosition, cameraGaze);
        Vector3.normalize(cameraGaze, cameraGaze);

        camera.computeViewVectors();
    }

    public void setInitialCamera() {
        camera.getPosition().set(-7, 4, -15);
        camera.getGaze().set(0, 0, 1);
        move(0, 0);
    }
}

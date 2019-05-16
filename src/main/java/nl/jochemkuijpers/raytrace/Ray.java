package nl.jochemkuijpers.raytrace;

import nl.jochemkuijpers.math.Vector3;

public class Ray {
    private final int depth;
    private final Vector3 origin;
    private final Vector3 heading;

    public Ray(int depth, Vector3 origin, Vector3 heading) {
        this.depth = depth;
        this.origin = new Vector3(origin);
        this.heading = new Vector3(heading);
    }

    public Ray(Ray other) {
        this(other.depth + 1, other.origin, other.heading);
    }

    public Ray(int depth) {
        this.depth = depth;
        this.origin = new Vector3();
        this.heading = new Vector3();
    }

    public int getDepth() {
        return depth;
    }

    public Vector3 getOrigin() {
        return origin;
    }

    public Vector3 getHeading() {
        return heading;
    }
}

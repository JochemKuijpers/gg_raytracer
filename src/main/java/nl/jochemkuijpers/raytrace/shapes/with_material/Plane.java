package nl.jochemkuijpers.raytrace.shapes.with_material;

import nl.jochemkuijpers.math.Intersections;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.materials.Material;

public class Plane extends ShapeWithMaterial {
    private final Vector3 position;
    private final Vector3 normal;

    public Plane(Vector3 position, Vector3 normal, Material material) {
        super(material);
        this.position = position;
        this.normal = normal;
    }

    @Override
    protected boolean computeIntersection(Ray ray, Vector3 outPosition, Vector3 outNormal) {
        float t = Intersections.plane(ray.getOrigin(), ray.getHeading(), position, normal);

        if (t < 0) return false;

        Vector3.addMul(ray.getOrigin(), ray.getHeading(), t, outPosition);
        outNormal.set(normal);
        if (Vector3.dot(ray.getHeading(), outNormal) > 0) {
            Vector3.mul(outNormal, -1, outNormal);
        }
        return true;
    }
}

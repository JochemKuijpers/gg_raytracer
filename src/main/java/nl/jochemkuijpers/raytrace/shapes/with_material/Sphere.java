package nl.jochemkuijpers.raytrace.shapes.with_material;

import nl.jochemkuijpers.math.Intersections;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.materials.Material;

public class Sphere extends ShapeWithMaterial {
    private final Vector3 position;
    private final float radius;

    public Sphere(Vector3 position, float radius, Material material) {
        super(material);
        this.position = position;
        this.radius = radius;
    }

    @Override
    protected boolean computeIntersection(Ray ray, Vector3 outPosition, Vector3 outNormal) {
        float t = Intersections.sphere(ray.getOrigin(), ray.getHeading(), position, radius);

        if (t < 0) { return false; }

        Vector3.addMul(ray.getOrigin(), ray.getHeading(), t, outPosition);
        Vector3.sub(outPosition, position, outNormal);
        Vector3.fastNormalize(outNormal, outNormal);

        return true;
    }
}

package nl.jochemkuijpers.raytrace.shapes.with_material;

import nl.jochemkuijpers.math.Intersections;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.materials.Material;

public class Box extends ShapeWithMaterial {
    private final Vector3 min;
    private final Vector3 max;

    public Box(Vector3 position, float size, Material material) {
        super(material);
        this.min = new Vector3();
        this.max = new Vector3();
        Vector3.addMul(position, Vector3.ONE, -size, this.min);
        Vector3.addMul(position, Vector3.ONE,  size, this.max);
    }

    public Box(Vector3 min, Vector3 max, Material material) {
        super(material);
        this.min = new Vector3(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z));
        this.max = new Vector3(Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    }

    @Override
    protected boolean computeIntersection(Ray ray, Vector3 outPosition, Vector3 outNormal) {
        float t = Intersections.box(ray.getOrigin(), ray.getHeading(), min, max);

        if (t < 0) { return false; }

        Vector3.addMul(ray.getOrigin(), ray.getHeading(), t, outPosition);

        Vector3 p = new Vector3();
        Vector3.add(min, max, p);
        Vector3.mul(p, 0.5f, p);
        Vector3.sub(outPosition, p, p);

        Vector3 d = new Vector3();
        Vector3.sub(min, max, d);
        Vector3.mul(d, 0.5f, d);

        float bias = 1.001f;
        outNormal.set(
                (int) (p.x / Math.abs(d.x) * bias),
                (int) (p.y / Math.abs(d.y) * bias),
                (int) (p.z / Math.abs(d.z) * bias)
        );
        Vector3.fastNormalize(outNormal, outNormal);

        return true;
    }
}

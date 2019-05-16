package nl.jochemkuijpers.raytrace.shapes.with_material;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Scene;
import nl.jochemkuijpers.raytrace.materials.Material;
import nl.jochemkuijpers.raytrace.shapes.Shape;

/**
 * This class factors out the common operation of looking up what color is produced by the material upon
 * intersection. All shapes that have their own material should extend this one.
 */
public abstract class ShapeWithMaterial implements Shape {
    protected final Material material;

    public ShapeWithMaterial(Material material) {
        this.material = material;
    }

    /**
     * Computes the first intersection point of the ray and the object
     * @param ray the input ray
     * @param outPosition the output position
     * @param outNormal the output normal
     * @return whether or not intersection took place
     */
    protected abstract boolean computeIntersection(Ray ray, Vector3 outPosition, Vector3 outNormal);

    @Override
    public boolean query(Ray ray, Scene world, Vector3 outPosition, Color outColor) {
        Vector3 intersecNormal = new Vector3();

        if (!computeIntersection(ray, outPosition, intersecNormal)) {
            return false;
        }

        material.queryColor(ray, world, outPosition, intersecNormal, outColor);
        return true;
    }
}

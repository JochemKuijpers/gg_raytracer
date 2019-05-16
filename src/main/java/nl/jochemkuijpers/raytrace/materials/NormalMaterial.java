package nl.jochemkuijpers.raytrace.materials;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;

/**
 * A material that emits the normal vector as a color. Was used for debugging.
 */
public class NormalMaterial implements Material {
    @Override
    public void queryColor(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        out.set(0.5f, 0.5f,0.5f);
        Vector3.addMul(out, normal, 0.5f, out);
    }
}

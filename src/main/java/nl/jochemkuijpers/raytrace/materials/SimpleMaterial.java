package nl.jochemkuijpers.raytrace.materials;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;

/** A very simple material; simply copies its color to the output. */
public class SimpleMaterial implements Material {
    private final Color color;

    public SimpleMaterial(Color color) {
        this.color = color;
    }

    @Override
    public void queryColor(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        out.set(color);
    }
}

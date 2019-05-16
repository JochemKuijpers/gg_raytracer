package nl.jochemkuijpers.raytrace.shapes;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;

public interface Shape {
    /**
     * Query the object whether it intersects or not.
     * If an intersection is found, the out variables must be set correctly.
     *
     * @param ray the input ray
     * @param world the world in which the query takes place (for recursive computations)
     * @param outPosition the intersected point (if return value true, otherwise undefined)
     * @param outColor the output color (if return value true, otherwise undefined)
     * @return whether or not an intersection took place
     */
    boolean query(Ray ray, Scene world, Vector3 outPosition, Color outColor);
}

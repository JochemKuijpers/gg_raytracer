package nl.jochemkuijpers.raytrace.materials;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Scene;

public interface Material {
    /**
     * Query the color of a material, possibly through recursive ray casts
     *
     * @param ray ray casted
     * @param world the world in which the ray was cast
     * @param position the position of the intersection
     * @param normal the normal of the intersection
     * @param out the output color
     */
    void queryColor(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out);
}

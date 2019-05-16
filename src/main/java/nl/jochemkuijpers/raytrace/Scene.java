package nl.jochemkuijpers.raytrace;

import nl.jochemkuijpers.math.Color;

/**
 * A scene turns a ray into a color.
 */
public interface Scene {
    int RECURSIVE_MAX_DEPTH = 3;

    /**
     * Query the world with a given ray.
     *
     * @param ray the input ray
     * @param out the output color
     * @return the distance to the intersection (negative for no intersection).
     */
    float query(Ray ray, Color out);
}

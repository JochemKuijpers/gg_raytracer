package nl.jochemkuijpers.math;

import org.apache.commons.math3.util.FastMath;

/**
 * All methods in this class compute the length along the heading vector, starting in the origin vector until the
 * intersection point. Negative values mean there is no intersection
 */
@SuppressWarnings("Duplicates")
public class Intersections {

    public static float box(Vector3 origin, Vector3 heading, Vector3 min, Vector3 max) {
        Vector3 headingFrac = new Vector3();
        headingFrac.x = 1f / heading.x;
        headingFrac.y = 1f / heading.y;
        headingFrac.z = 1f / heading.z;

        float tmin = (min.x - origin.x) * headingFrac.x;
        float tmax = (max.x - origin.x) * headingFrac.x;
        float swap;

        if (tmin > tmax) { swap = tmin; tmin = tmax; tmax = swap; }

        float tymin = (min.y - origin.y) * headingFrac.y;
        float tymax = (max.y - origin.y) * headingFrac.y;

        if (tymin > tymax) { swap = tymin; tymin = tymax; tymax = swap; }

        if ((tmin > tymax) || (tymin > tmax))
            return -1f;

        if (tymin > tmin)
            tmin = tymin;

        if (tymax < tmax)
            tmax = tymax;

        float tzmin = (min.z - origin.z) * headingFrac.z;
        float tzmax = (max.z - origin.z) * headingFrac.z;

        if (tzmin > tzmax) { swap = tzmin; tzmin = tzmax; tzmax = swap; }

        if ((tmin > tzmax) || (tzmin > tmax))
            return -1f;

        if (tzmin > tmin)
            tmin = tzmin;

        if (tzmax < tmax)
            tmax = tzmax;

        return tmin < 0 ? tmax : tmin;
    }

    public static float sphere(Vector3 origin, Vector3 heading, Vector3 position, float radius) {
        // intersection algorithm based on work available at scratchapixel.com
        Vector3 L = new Vector3();
        Vector3.sub(position, origin, L);
        float tca = Vector3.dot(L, heading);
        float d2 = L.magSqr() - tca * tca;
        float radius2 = radius * radius;
        if (d2 > radius2) return -1f;

        float thc = (float) FastMath.sqrt(radius2 - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;

        if (t0 > t1) {
            float ttmp = t1; t1 = t0; t0 = ttmp;
        }

        if (t0 < 0) {
            if (t1 < 0) return -1f;
            t0 = t1;
        }
        return t0;
    }

    public static float plane(Vector3 origin, Vector3 heading, Vector3 position, Vector3 normal) {
        float d = Vector3.dot(normal, heading);
        if (d == 0) return -1f;

        Vector3 difference = new Vector3();
        Vector3.sub(position, origin, difference);
        return Vector3.dot(difference, normal) / d;
    }
}

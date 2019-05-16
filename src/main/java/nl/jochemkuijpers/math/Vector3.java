package nl.jochemkuijpers.math;

import org.apache.commons.math3.util.FastMath;

/**
 * This class has public members and static methods to reduce OOP overhead, since the bottlenecks come from doing lots
 * of operations on Vector3s. This allows users to be more thoughtful about memory allocations.
 */
public class Vector3 {
    public static Vector3 ZERO   = new Vector3();
    public static Vector3 ONE    = new Vector3(1, 1, 1);
    public static Vector3 XIDENT = new Vector3(1, 0, 0);
    public static Vector3 YIDENT = new Vector3(0, 1, 0);
    public static Vector3 ZIDENT = new Vector3(0, 0, 1);

    public Vector3() {}

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public float x = 0;
    public float y = 0;
    public float z = 0;

    public void set(Vector3 a) {
        x = a.x;
        y = a.y;
        z = a.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** c = a + b */
    public static void add(Vector3 a, Vector3 b, Vector3 c) {
        c.x = a.x + b.x;
        c.y = a.y + b.y;
        c.z = a.z + b.z;
    }

    /** c = a - b */
    public static void sub(Vector3 a, Vector3 b, Vector3 c) {
        c.x = a.x - b.x;
        c.y = a.y - b.y;
        c.z = a.z - b.z;
    }

    /** c = a * s */
    public static void mul(Vector3 a, float s, Vector3 c) {
        c.x = a.x * s;
        c.y = a.y * s;
        c.z = a.z * s;
    }

    /** c = a + b * s */
    public static void addMul(Vector3 a, Vector3 b, float s, Vector3 c) {
        c.x = a.x + b.x * s;
        c.y = a.y + b.y * s;
        c.z = a.z + b.z * s;
    }

    /** c = a × b */
    public static void cross(Vector3 a, Vector3 b, Vector3 c) {
        float t1, t2;
        t1 = a.y * b.z - a.z * b.y;
        t2 = a.z * b.x - a.x * b.z;
        c.x = t1;
        c.y = t2;
        c.z = a.x * b.y - a.y * b.x;
    }

    /** b = a / |a| */
    public static void fastNormalize(Vector3 a, Vector3 b) {
        float invLen = a.invMag();
        b.x = a.x * invLen;
        b.y = a.y * invLen;
        b.z = a.z * invLen;
    }

    /** b = a / |a| */
    public static void normalize(Vector3 a, Vector3 b) {
        float invLen = 1f / a.mag();
        b.x = a.x * invLen;
        b.y = a.y * invLen;
        b.z = a.z * invLen;
    }

    /** e = lerp(lerp(a, b, tx), lerp(c, d, tx), ty); */
    public static void lerp2d(Vector3 a, Vector3 b, Vector3 c, Vector3 d, float tx, float ty, Vector3 e) {
        e.x = (a.x * (1 - tx) + b.x * tx) * (1 - ty) + (c.x * (1 - tx) + d.x * tx) * ty;
        e.y = (a.y * (1 - tx) + b.y * tx) * (1 - ty) + (c.y * (1 - tx) + d.y * tx) * ty;
        e.z = (a.z * (1 - tx) + b.z * tx) * (1 - ty) + (c.z * (1 - tx) + d.z * tx) * ty;
    }

    /** return a · b */
    public static float dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    /** return |a - b|^2 */
    public static float distSqr(Vector3 a, Vector3 b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z);
    }

    /** return mag(this)^2, cheaper than mag(a) */
    public float magSqr() {
        return x * x + y * y + z * z;
    }

    /** return mag(this) */
    public float mag() {
        return (float) FastMath.sqrt(magSqr());
    }

    /** return fisqrt(mag(this)^2) */
    public float invMag() {
        return MathUtils.fisqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}

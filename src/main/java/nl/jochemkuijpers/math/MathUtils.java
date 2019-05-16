package nl.jochemkuijpers.math;

public class MathUtils {
    /**
     * Fast inverse square root. Approximates <code>1/sqrt(x)</code> with an acceptable error.
     * @param x input value
     * @return approximation for <code>1/sqrt(x)</code>
     */
    static float fisqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits( x );
        i = 0x5f3759df - (i >> 1);
        x = Float.intBitsToFloat( i );
        x = x * (1.5f - (xhalf * x * x));
        return x;
    }

    public static float clamp(float min, float x, float max) {
        if (x < min) return min;
        if (x > max) return max;
        return x;
    }
}

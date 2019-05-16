package nl.jochemkuijpers.math;

import nl.jochemkuijpers.math.MathUtils;

import static org.junit.Assert.*;

public class MathUtilsTest {

    @org.junit.Test
    public void fisqrt() {
        // tests a bunch of floats in expected orders of magnitude
        // fisqrt should never deviate more than acceptableDelta
        float acceptableDelta = 0.002f;

        for (float x = 1e-32f; x < 1e32f; x *= 1.00001f) {
            float fisqrt = MathUtils.fisqrt(x);
            float expected = 1f / (float) Math.sqrt(x);
            assertEquals(expected, fisqrt, acceptableDelta * fisqrt);
        }
    }
}
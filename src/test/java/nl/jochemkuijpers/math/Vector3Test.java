package nl.jochemkuijpers.math;

import org.junit.Assert;
import org.junit.Test;

public class Vector3Test {

    private static void assertEquals(Vector3 expected, Vector3 actual) {
        float acceptableDelta = 0.002f * Math.max(expected.x, Math.max(expected.y, expected.z));
        Assert.assertEquals(expected.x, actual.x, acceptableDelta);
        Assert.assertEquals(expected.y, actual.y, acceptableDelta);
        Assert.assertEquals(expected.z, actual.z, acceptableDelta);
    }

    @Test
    public void add() {
        Vector3 a = new Vector3();
        Vector3 b = new Vector3();
        Vector3 c = new Vector3();
        Vector3 e;

        Vector3.add(a, b, c);
        assertEquals(Vector3.ZERO, c);

        a = new Vector3(0.25f, 0.5f, 0.75f);
        b = new Vector3(0.66f, 0.5f, 0.33f);
        Vector3.add(a, b, c);
        e = new Vector3(0.25f + 0.66f, 0.5f + 0.5f, 0.75f + 0.33f);
        assertEquals(e, c);

        e = new Vector3((0.25f + 0.66f) * 2, (0.5f + 0.5f) * 2, (0.75f + 0.33f) * 2);
        Vector3.add(c, c, c);
        assertEquals(e, c);
    }

    @Test
    public void sub() {
        Vector3 a = new Vector3();
        Vector3 b = new Vector3();
        Vector3 c = new Vector3();
        Vector3 e;

        Vector3.sub(a, b, c);
        assertEquals(Vector3.ZERO, c);

        a = new Vector3(0.25f, 0.5f, 0.75f);
        b = new Vector3(0.66f, 0.5f, 0.33f);
        Vector3.sub(a, b, c);
        e = new Vector3(0.25f - 0.66f, 0.5f - 0.5f, 0.75f - 0.33f);
        assertEquals(e, c);
        assertEquals(new Vector3(0.25f, 0.5f, 0.75f), a);
        assertEquals(new Vector3(0.66f, 0.5f, 0.33f), b);

        e = Vector3.ZERO;
        Vector3.sub(c, c, c);
        assertEquals(e, c);
    }

    @Test
    public void mul() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        float s = -2.0295f;
        Vector3 b = new Vector3();
        Vector3 e = new Vector3(a.x * s, a.y * s, a.z * s);
        Vector3.mul(a, s, b);
        assertEquals(e, b);
        assertEquals(new Vector3(0.125f, 0.628f, 0.289f), a);
    }

    @Test
    public void addMul() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        float s = -2.0295f;
        Vector3 b = new Vector3(0.592f, -0.492f, 1.592f);
        Vector3 c = new Vector3();
        Vector3 e = new Vector3(a.x + b.x * s, a.y + b.y * s, a.z + b.z * s);
        Vector3.addMul(a, b, s, c);
        assertEquals(e, c);
        assertEquals(new Vector3(0.125f, 0.628f, 0.289f), a);
        assertEquals(new Vector3(0.592f, -0.492f, 1.592f), b);
    }

    @Test
    public void cross() {
        Vector3 a = Vector3.XIDENT;
        Vector3 b = Vector3.YIDENT;
        Vector3 c = new Vector3();
        Vector3 e = Vector3.ZIDENT;
        Vector3.cross(a, b, c);
        assertEquals(e, c);
    }

    @Test
    public void normalizeAndFastNormalize() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        Vector3 b = new Vector3();
        Vector3 e = new Vector3(0.178f, 0.894f, 0.411f);
        Vector3.fastNormalize(a, b);
        assertEquals(e, b);
        Vector3.fastNormalize(a, b);
        assertEquals(e, b);
        assertEquals(new Vector3(0.125f, 0.628f, 0.289f), a);
    }

    @Test
    public void dot() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        Vector3 b = new Vector3(0.592f, -0.492f, 1.592f);
        Assert.assertEquals(1.0f, Vector3.dot(Vector3.XIDENT, Vector3.XIDENT), 0.00001f);
        Assert.assertEquals(0.0f, Vector3.dot(Vector3.XIDENT, Vector3.ZIDENT), 0.00001f);
        Assert.assertEquals(0.22512f, Vector3.dot(a, b), 0.00001f);
        assertEquals(new Vector3(0.125f, 0.628f, 0.289f), a);
        assertEquals(new Vector3(0.592f, -0.492f, 1.592f), b);
    }

    @Test
    public void magSqr() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        Assert.assertEquals(0.493530f, a.magSqr(), 0.00001f);
    }

    @Test
    public void mag() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        Assert.assertEquals(0.702517f, a.mag(), 0.00001f);
    }

    @Test
    public void invMag() {
        Vector3 a = new Vector3(0.125f, 0.628f, 0.289f);
        Assert.assertEquals(1/0.702517f, a.invMag(), 0.01f);
        a = Vector3.XIDENT;
        Assert.assertEquals(1, a.invMag(), 0.01f);
    }
}
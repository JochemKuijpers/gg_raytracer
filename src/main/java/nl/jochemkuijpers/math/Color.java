package nl.jochemkuijpers.math;

public class Color extends Vector3 {

    public final static Color WHITE   = new Color(1, 1, 1);
    public final static Color GRAY    = new Color(0.8f, 0.8f, 0.8f);
    public final static Color BLACK   = new Color(0, 0, 0);
    public final static Color RED     = new Color(0.8f, 0.1f, 0.1f);
    public final static Color ORANGE  = new Color(0.8f, 0.4f, 0.1f);
    public final static Color YELLOW  = new Color(0.7f, 0.7f, 0.1f);
    public final static Color LIME    = new Color(0.4f, 0.8f, 0.1f);
    public final static Color GREEN   = new Color(0.1f, 0.8f, 0.1f);
    public final static Color AQUA    = new Color(0.1f, 0.7f, 0.7f);
    public final static Color BLUE    = new Color(0.1f, 0.1f, 0.8f);
    public final static Color FUCHIA  = new Color(0.7f, 0.1f, 0.7f);

    /**
     * Array of mulitpliers used for a 4×4 dithering pattern. By using multiplicative biases instead of additive biases,
     * dithering is about equally strong for bright as dim colors.
     */
    private static float[] ditherbias = {
            0.99632353f, 0.99954044f, 1.00321691f, 0.99862132f,
            1.00229779f, 0.99770221f, 1.00137868f, 0.99678309f,
            1.00045956f, 1.00367647f, 0.99908088f, 1.00275735f,
            0.99816176f, 1.00183824f, 0.99724265f, 1.00091912f
    };

    /**
     * Lookup table for gamma encoding. These values are linearly interpolated from a float value with the nearest two
     * integer indices.
     *
     * The table is 257 entries long to avoid dealing with the case where the float index is exactly 255.
     */
    private static int[] gammaInv = {
              0,  21,  28,  34,  39,  43,  46,  50,  53,  56,  59,  61,  64,  66,  68,  70,  72,  74,  76,  78,  80,
             82,  84,  85,  87,  89,  90,  92,  93,  95,  96,  98,  99, 101, 102, 103, 105, 106, 107, 109, 110, 111,
            112, 114, 115, 116, 117, 118, 119, 120, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134,
            135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 144, 145, 146, 147, 148, 149, 150, 151, 151, 152, 153,
            154, 155, 156, 156, 157, 158, 159, 160, 160, 161, 162, 163, 164, 164, 165, 166, 167, 167, 168, 169, 170,
            170, 171, 172, 173, 173, 174, 175, 175, 176, 177, 178, 178, 179, 180, 180, 181, 182, 182, 183, 184, 184,
            185, 186, 186, 187, 188, 188, 189, 190, 190, 191, 192, 192, 193, 194, 194, 195, 195, 196, 197, 197, 198,
            199, 199, 200, 200, 201, 202, 202, 203, 203, 204, 205, 205, 206, 206, 207, 207, 208, 209, 209, 210, 210,
            211, 212, 212, 213, 213, 214, 214, 215, 215, 216, 217, 217, 218, 218, 219, 219, 220, 220, 221, 221, 222,
            223, 223, 224, 224, 225, 225, 226, 226, 227, 227, 228, 228, 229, 229, 230, 230, 231, 231, 232, 232, 233,
            233, 234, 234, 235, 235, 236, 236, 237, 237, 238, 238, 239, 239, 240, 240, 241, 241, 242, 242, 243, 243,
            244, 244, 245, 245, 246, 246, 247, 247, 248, 248, 249, 249, 249, 250, 250, 251, 251, 252, 252, 253, 253,
            254, 254, 255, 255, 255
    };

    public Color() { super(); }

    public Color(float r, float g, float b) { super(r, g, b); }

    public Color(Color other) { super(other); }

    /**
     * Compute the sRGB color values appropriate for the given Color using gamma correction and dithering
     * @param a input color
     * @param x position of the pixel on the screen
     * @param y position of the pixel on the screen
     * @return ARGB color value
     */
    public static int gammaEncode(Color a, int x, int y) {
        float bias = ditherbias[(y % 4) * 4 + x % 4];

        float r = MathUtils.clamp(0f, (a.x * bias) * 255f, 255f);
        float g = MathUtils.clamp(0f, (a.y * bias) * 255f, 255f);
        float b = MathUtils.clamp(0f, (a.z * bias) * 255f, 255f);

        int rt = (int) ((r - (int) r) * 256);
        int gt = (int) ((g - (int) g) * 256);
        int bt = (int) ((b - (int) b) * 256);

        // linear interpolation is done as a 256-integer multiple to avoid expensive float division
        int ri = (gammaInv[(int) r] * (256 - rt) + gammaInv[(int) r + 1] * rt) / 256;
        int gi = (gammaInv[(int) g] * (256 - gt) + gammaInv[(int) g + 1] * gt) / 256;
        int bi = (gammaInv[(int) b] * (256 - bt) + gammaInv[(int) b + 1] * bt) / 256;

        return 0xFF000000 | ri << 16 | gi << 8 | bi;
    }

    /**
     * Create a color from HSL
     * @param hue 0 - 360
     * @param saturation 0 - 1
     * @param lightness 0 - 1
     * @return new color
     */
    public static Color createHSL(float hue, float saturation, float lightness) {

        hue = (hue % 360f + 360f) % 360f;
        saturation = MathUtils.clamp(0f, saturation, 1f);
        lightness = MathUtils.clamp(0f, lightness, 1f);

        float C = (1f - Math.abs(2f * lightness - 1f)) * saturation;
        float X = C * (1f - Math.abs((hue / 60f) % 2f - 1f));
        float m = lightness - C / 2f;

        float r, g, b;

        if (hue <  60f) { r = C; g = X; b = 0; } else
        if (hue < 120f) { r = X; g = C; b = 0; } else
        if (hue < 180f) { r = 0; g = C; b = X; } else
        if (hue < 240f) { r = 0; g = X; b = C; } else
        if (hue < 300f) { r = X; g = 0; b = C; } else
                        { r = C; g = 0; b = X; }

        r += m; g += m; b += m;

        return new Color(r, g, b);
    }
}

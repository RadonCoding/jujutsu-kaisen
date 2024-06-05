package radon.jujutsu_kaisen.util;


import radon.jujutsu_kaisen.data.capability.IJujutsuCapability;
import org.joml.Vector3f;

public class ColorUtil {
    public static Vector3f interpolate(Vector3f start, Vector3f end, float factor) {
        float[] hslStart = rgbToHsl(start);
        float[] hslEnd = rgbToHsl(end);

        float hue = hslStart[0] + (hslEnd[0] - hslStart[0]) * factor;

        if (hue < 0.0F) {
            hue += 1.0F;
        } else if (hue > 1.0F) {
            hue -= 1.0F;
        }

        float saturation = hslStart[1] + (hslEnd[1] - hslStart[1]) * factor;
        float lightness = hslStart[2] + (hslEnd[2] - hslStart[2]) * factor;

        return hslToRgb(hue, saturation, lightness);
    }

    public static float[] rgbToHsl(Vector3f rgb) {
        float r = rgb.x / 255.0F;
        float g = rgb.y / 255.0F;
        float b = rgb.z / 255.0F;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));

        float h, s, l;
        h = s = l = (max + min) / 2;

        if (max == min) {
            h = s = 0;
        } else {
            float d = max - min;
            s = l > 0.5F ? d / (2 - max - min) : d / (max + min);
            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else if (max == b) h = (r - g) / d + 4;
            h /= 6;
        }
        return new float[]{h, s, l};
    }

    public static Vector3f hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5F ? l * (1.0F + s) : l + s - l * s;
            float p = 2.0F * l - q;
            r = hueToRGB(p, q, h + 1.0F / 3.0F);
            g = hueToRGB(p, q, h);
            b = hueToRGB(p, q, h - 1.0F / 3.0F);
        }
        return new Vector3f(r * 255.0F, g * 255.0F, b * 255.0F);
    }

    public static float hueToRGB(float p, float q, float t) {
        if (t < 0.0F) t += 1.0F;
        if (t > 1.0F) t -= 1.0F;
        if (t < 1.0F / 6.0F) return p + (q - p) * 6.0F * t;
        if (t < 0.5F) return q;
        if (t < 2.0F / 3.0F) return p + (q - p) * (2.0F / 3.0F - t) * 6.0F;
        return p;
    }
}

package ca.dalezak.android.base.utils;

import android.graphics.Color;

public class Colors {

    public static int darken(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    public static int lighten(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1.0f - factor * (1.0f - hsv[2]);
        return Color.HSVToColor(hsv);
    }

    public static int shade(int color, float percentage) {
        int a = (int)(255 * percentage);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, r, g, b);
    }

}

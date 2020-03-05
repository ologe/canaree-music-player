package dev.olog.shared.android.palette;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import dev.olog.shared.android.extensions.ContextExtensionKt;
import timber.log.Timber;

public class ColorUtil {

    private static final String TAG = "ColorUtil";
    private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal<>();

    /**
     * Returns the luminance of a color as a float between {@code 0.0} and {@code 1.0}.
     * <p>Defined as the Y component in the XYZ representation of {@code color}.</p>
     */
    public static double calculateLuminance(int backgroundColor) {
        return ColorUtils.calculateLuminance(backgroundColor);
    }

    public static int getAccentColor(Context context, Palette palette) {
        int mutedColor = palette.getMutedColor(ContextExtensionKt.colorAccent(context));
        int lightVibrant = palette.getLightVibrantColor(mutedColor);
        return palette.getVibrantColor(lightVibrant);
    }

//    @ColorInt
//    public static int shiftBackgroundColorForLightText(@ColorInt int backgroundColor) {
//        while (isColorLightSecondVersion(backgroundColor)) {
//            backgroundColor = darkenColor(backgroundColor);
//        }
//        return backgroundColor;
//    }

    /**
     * Returns the contrast ratio between {@code foreground} and {@code background}.
     * {@code background} must be opaque.
     * <p>
     * Formula defined
     * <a href="http://www.w3.org/TR/2008/REC-WCAG20-20081211/#contrast-ratiodef">here</a>.
     */
    public static double calculateContrast(@ColorInt int foreground, @ColorInt int background) {
        if (Color.alpha(background) != 255) {
            Timber.wtf(TAG +"background can not be translucent: #"
                    + Integer.toHexString(background));
        }
        if (Color.alpha(foreground) < 255) {
            // If the foreground is translucent, composite the foreground over the background
            foreground = compositeColors(foreground, background);
        }
        final double luminance1 = calculateLuminance(foreground) + 0.05;
        final double luminance2 = calculateLuminance(background) + 0.05;
        // Now return the lighter luminance divided by the darker luminance
        return Math.max(luminance1, luminance2) / Math.min(luminance1, luminance2);
    }

    /**
     * Composite two potentially translucent colors over each other and returns the result.
     */
    public static int compositeColors(@ColorInt int foreground, @ColorInt int background) {
        int bgAlpha = Color.alpha(background);
        int fgAlpha = Color.alpha(foreground);
        int a = compositeAlpha(fgAlpha, bgAlpha);
        int r = compositeComponent(Color.red(foreground), fgAlpha,
                Color.red(background), bgAlpha, a);
        int g = compositeComponent(Color.green(foreground), fgAlpha,
                Color.green(background), bgAlpha, a);
        int b = compositeComponent(Color.blue(foreground), fgAlpha,
                Color.blue(background), bgAlpha, a);
        return Color.argb(a, r, g, b);
    }

    private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
        return 0xFF - (((0xFF - backgroundAlpha) * (0xFF - foregroundAlpha)) / 0xFF);
    }

    private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a) {
        if (a == 0) return 0;
        return ((0xFF * fgC * fgA) + (bgC * bgA * (0xFF - fgA))) / (a * 0xFF);
    }

    public static boolean satisfiesTextContrast(int backgroundColor, int foregroundColor) {
        return calculateContrast(foregroundColor, backgroundColor) >= 4.5;
    }

    /**
     * Finds a suitable color such that there's enough contrast.
     *
     * @param color the color to start searching from.
     * @param other the color to ensure contrast against. Assumed to be lighter than {@param color}
     * @param findFg if true, we assume {@param color} is a foreground, otherwise a background.
     * @param minRatio the minimum contrast ratio required.
     * @return a color with the same hue as {@param color}, potentially darkened to meet the
     *          contrast ratio.
     */
    public static int findContrastColor(int color, int other, boolean findFg, double minRatio) {
        int fg = findFg ? color : other;
        int bg = findFg ? other : color;
        if (ColorUtils.calculateContrast(fg, bg) >= minRatio) {
            return color;
        }
        double[] lab = new double[3];
        ColorUtils.colorToLAB(findFg ? fg : bg, lab);
        double low = 0, high = lab[0];
        final double a = lab[1], b = lab[2];
        for (int i = 0; i < 15 && high - low > 0.00001; i++) {
            final double l = (low + high) / 2;
            if (findFg) {
                fg = ColorUtils.LABToColor(l, a, b);
            } else {
                bg = ColorUtils.LABToColor(l, a, b);
            }
            if (ColorUtils.calculateContrast(fg, bg) > minRatio) {
                low = l;
            } else {
                high = l;
            }
        }
        return ColorUtils.LABToColor(low, a, b);
    }

    /**
     * Change a color by a specified value
     * @param baseColor the base color to lighten
     * @param amount the amount to lighten the color from 0 to 100. This corresponds to the L
     *               increase in the LAB color space. A negative value will darken the color and
     *               a positive will lighten it.
     * @return the changed color
     */
    public static int changeColorLightness(int baseColor, int amount) {
        final double[] result = getTempDouble3Array();
        ColorUtils.colorToLAB(baseColor, result);
        result[0] = Math.max(Math.min(100, result[0] + amount), 0);
        return ColorUtils.LABToColor(result[0], result[1], result[2]);
    }

    private static double[] getTempDouble3Array() {
        double[] result = TEMP_ARRAY.get();
        if (result == null) {
            result = new double[3];
            TEMP_ARRAY.set(result);
        }
        return result;
    }

    /**
     * Finds a suitable color such that there's enough contrast.
     *
     * @param color the color to start searching from.
     * @param other the color to ensure contrast against. Assumed to be darker than {@param color}
     * @param findFg if true, we assume {@param color} is a foreground, otherwise a background.
     * @param minRatio the minimum contrast ratio required.
     * @return a color with the same hue as {@param color}, potentially darkened to meet the
     *          contrast ratio.
     */
    public static int findContrastColorAgainstDark(int color, int other, boolean findFg,
                                                   double minRatio) {
        int fg = findFg ? color : other;
        int bg = findFg ? other : color;
        if (ColorUtils.calculateContrast(fg, bg) >= minRatio) {
            return color;
        }
        float[] hsl = new float[3];
        ColorUtils.colorToHSL(findFg ? fg : bg, hsl);
        float low = hsl[2], high = 1;
        for (int i = 0; i < 15 && high - low > 0.00001; i++) {
            final float l = (low + high) / 2;
            hsl[2] = l;
            if (findFg) {
                fg = ColorUtils.HSLToColor(hsl);
            } else {
                bg = ColorUtils.HSLToColor(hsl);
            }
            if (ColorUtils.calculateContrast(fg, bg) > minRatio) {
                high = l;
            } else {
                low = l;
            }
        }
        return findFg ? fg : bg;
    }

    public static boolean isColorLight(int backgroundColor) {
        return calculateLuminance(backgroundColor) > 0.5f;
    }

    private static boolean isColorLightSecondVersion(@ColorInt int color) {
        double darkness = 1.0D - (0.299D * (double)Color.red(color) + 0.587D * (double)Color.green(color) + 0.114D * (double)Color.blue(color)) / 255.0D;
        return darkness < 0.4D;
    }

    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9F);
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0D,to = 2.0D) float by) {
        if (by == 1.0F) {
            return color;
        } else {
            int alpha = Color.alpha(color);
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= by;
            return (alpha << 24) + (16777215 & Color.HSVToColor(hsv));
        }
    }

}

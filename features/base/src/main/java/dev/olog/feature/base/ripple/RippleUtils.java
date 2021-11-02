package dev.olog.feature.base.ripple;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

public class RippleUtils {

    public static RippleDrawable create(@Nullable Palette palette,
                                        @FloatRange(from = 0f, to = 1f) float darkAlpha,
                                        @FloatRange(from = 0f, to = 1f) float lightAlpha,
                                        @ColorInt int fallbackColor,
                                        boolean bounded) {

        int rippleColor = createColor(palette, darkAlpha, lightAlpha, fallbackColor);

        return new RippleDrawable(ColorStateList.valueOf(rippleColor), null,
                bounded ? new ColorDrawable(Color.WHITE) : null);
    }

    public static int createColor(@Nullable Palette palette,
                                  @FloatRange(from = 0f, to = 1f) float darkAlpha,
                                  @FloatRange(from = 0f, to = 1f) float lightAlpha,
                                  @ColorInt int fallbackColor){

        int rippleColor = fallbackColor;
        if (palette != null) {
            // try the named swatches in preference order
            if (palette.getVibrantSwatch() != null) {
                rippleColor = modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

            } else if (palette.getLightVibrantSwatch() != null) {
                rippleColor = modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
                        lightAlpha);
            } else if (palette.getDarkVibrantSwatch() != null) {
                rippleColor = modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
                        darkAlpha);
            } else if (palette.getMutedSwatch() != null) {
                rippleColor = modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
            } else if (palette.getLightMutedSwatch() != null) {
                rippleColor = modifyAlpha(palette.getLightMutedSwatch().getRgb(),
                        lightAlpha);
            } else if (palette.getDarkMutedSwatch() != null) {
                rippleColor =
                        modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
            }
        }

        return rippleColor;
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult
    @ColorInt int modifyAlpha(@ColorInt int color,
                              @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     */
    public static @CheckResult
    @ColorInt
    int modifyAlpha(@ColorInt int color,
                    @FloatRange(from = 0f, to = 1f) float alpha) {
        return modifyAlpha(color, (int) (255f * alpha));
    }

}

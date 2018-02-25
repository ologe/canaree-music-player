package dev.olog.msc.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

public class ViewUtils {

    public static RippleDrawable createRipple(@Nullable Palette palette,
                                              @FloatRange(from = 0f, to = 1f) float darkAlpha,
                                              @FloatRange(from = 0f, to = 1f) float lightAlpha,
                                              @ColorInt int fallbackColor,
                                              boolean bounded) {

        int rippleColor = createRippleColor(palette, darkAlpha, lightAlpha, fallbackColor);

        return new RippleDrawable(ColorStateList.valueOf(rippleColor), null,
                bounded ? new ColorDrawable(Color.WHITE) : null);
    }

    public static int createRippleColor(@Nullable Palette palette,
                                                   @FloatRange(from = 0f, to = 1f) float darkAlpha,
                                                   @FloatRange(from = 0f, to = 1f) float lightAlpha,
                                                   @ColorInt int fallbackColor){
        int rippleColor = fallbackColor;
        if (palette != null) {
            // try the named swatches in preference order
            if (palette.getVibrantSwatch() != null) {
                rippleColor =
                        ColorUtils.modifyAlpha(palette.getVibrantSwatch().getRgb(), darkAlpha);

            } else if (palette.getLightVibrantSwatch() != null) {
                rippleColor = ColorUtils.modifyAlpha(palette.getLightVibrantSwatch().getRgb(),
                        lightAlpha);
            } else if (palette.getDarkVibrantSwatch() != null) {
                rippleColor = ColorUtils.modifyAlpha(palette.getDarkVibrantSwatch().getRgb(),
                        darkAlpha);
            } else if (palette.getMutedSwatch() != null) {
                rippleColor = ColorUtils.modifyAlpha(palette.getMutedSwatch().getRgb(), darkAlpha);
            } else if (palette.getLightMutedSwatch() != null) {
                rippleColor = ColorUtils.modifyAlpha(palette.getLightMutedSwatch().getRgb(),
                        lightAlpha);
            } else if (palette.getDarkMutedSwatch() != null) {
                rippleColor =
                        ColorUtils.modifyAlpha(palette.getDarkMutedSwatch().getRgb(), darkAlpha);
            }
        }

        return rippleColor;
    }

}

package dev.olog.presentation.widgets.equalizer.croller;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

class Utils {

    static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}

package dev.olog.shared.utils

import androidx.core.graphics.ColorUtils

object ColorUtils {

    @Suppress("NOTHING_TO_INLINE")
    inline fun desaturate(color: Int, amount: Float = .3f): Int {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        if (hsl[1] > .5f) {
            hsl[1] = clamp(hsl[1] - amount, .5f, 1f)
        }
        return ColorUtils.HSLToColor(hsl)
    }

}
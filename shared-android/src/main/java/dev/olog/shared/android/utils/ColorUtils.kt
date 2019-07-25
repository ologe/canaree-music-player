package dev.olog.shared.android.utils

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.math.MathUtils.clamp

object ColorUtils {

    // minimum saturation in color accents ~0.75f
    const val MIN_SATURATION = 0.75f

    @Suppress("NOTHING_TO_INLINE")
    @JvmStatic
    inline fun desaturate(color: Int, amount: Float = .25f): Int {
        if (color == Color.TRANSPARENT) {
            return color
        }
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color, hsl)
        if (hsl[1] > MIN_SATURATION) {
            hsl[1] = clamp(
                hsl[1] - amount,
                MIN_SATURATION - 0.1f,
                1f
            )
        }
        return ColorUtils.HSLToColor(hsl)
    }

}
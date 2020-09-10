package dev.olog.shared.components.extension

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red

fun Color.desaturate(
    amount: Float = 0.25f,
    minDesaturation: Float = 0.75f
): Color {
    if (this == Color.Transparent) {
        // can't desaturate transparent color
        return this
    }

    val originalAlpha = this.alpha
    if (originalAlpha == 0f) {
        // can't desaturate transparent color
        return this
    }

    val colorWithFullAlpha = this.copy(alpha = 1f)

    // desaturate opaque color
    val hsl = colorWithFullAlpha.toHSL()
    if (hsl[1] > minDesaturation) {
        hsl[1] = (hsl[1] - amount).coerceIn(minDesaturation, 1f)
    }

    val desaturatedArgbColor = ColorUtils.HSLToColor(hsl)
    val desaturatedColor = Color(desaturatedArgbColor)
    return desaturatedColor.copy(alpha = originalAlpha)
}

// port from ColorUtils.colorToHSL
fun Color.toHSL(): FloatArray {
    val argb = this.toArgb()
    val out = FloatArray(3) { 0f }
    ColorUtils.RGBToHSL(argb.red, argb.green, argb.blue, out)
    return out
}

/**
 * Return the red component of a color int. This is the same as saying
 * (color >> 16) & 0xFF
 */
private fun Int.toHex(): Int {
    return this shr 16 and 0xFF
}
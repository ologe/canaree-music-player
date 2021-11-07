package dev.olog.msc.settings

import android.graphics.Color
import dev.olog.lib.ColorDesaturationUtils

object ColorPalette {

    @JvmStatic
    fun getAccentColors(isDarkMode: Boolean): IntArray {
        if (isDarkMode) {
            return ACCENT_COLORS_DESATURATED
        }
        return ACCENT_COLORS
    }

    @JvmStatic
    fun getAccentColorsSub(isDarkMode: Boolean): Array<IntArray> {
        if (isDarkMode) {
            return ACCENT_COLORS_SUB_DESATURATED
        }
        return ACCENT_COLORS_SUB
    }

    fun getRealAccentSubColor(isDarkMode: Boolean, color: Int): Int {
        if (!isDarkMode) {
            return color
        }
        for (i in 0 until ACCENT_COLORS_SUB_DESATURATED.size){
            for (j in 0 until ACCENT_COLORS_SUB_DESATURATED[i].size){
                val currentColor = ACCENT_COLORS_SUB_DESATURATED[i][j]
                if (currentColor == color){
                    return ACCENT_COLORS_SUB[i][j]
                }
            }
        }
        throw IllegalStateException("found must be found above, is dark mode=$isDarkMode, color=$color")
    }

    @JvmStatic
    private val ACCENT_COLORS_DESATURATED: IntArray by lazy {
        ACCENT_COLORS.map { ColorDesaturationUtils.desaturate(it, .25f, .75f) }.toIntArray()
    }

    @JvmStatic
    private val ACCENT_COLORS_SUB_DESATURATED: Array<IntArray> by lazy {
        ACCENT_COLORS_SUB.map { ints ->
            ints.copyOf().map { ColorDesaturationUtils.desaturate(it, 0.25f, 0.75f) }.toIntArray()
        }.toTypedArray()
    }

    @JvmStatic
    private val ACCENT_COLORS = intArrayOf(
        Color.parseColor("#FF1744"),
        Color.parseColor("#F50057"),
        Color.parseColor("#D500F9"),
        Color.parseColor("#651FFF"),
        Color.parseColor("#3D5AFE"),
        Color.parseColor("#2979FF"),
        Color.parseColor("#00B0FF"),
        Color.parseColor("#00E5FF"),
        Color.parseColor("#1DE9B6"),
        Color.parseColor("#00E676"),
        Color.parseColor("#76FF03"),
        Color.parseColor("#C6FF00"),
        Color.parseColor("#FFEA00"),
        Color.parseColor("#FFC400"),
        Color.parseColor("#FF9100"),
        Color.parseColor("#FF3D00")
    )

    @JvmStatic
    private val ACCENT_COLORS_SUB = arrayOf(
        intArrayOf(
            Color.parseColor("#FF8A80"),
            Color.parseColor("#FF5252"),
            Color.parseColor("#FF1744"),
            Color.parseColor("#D50000")
        ),
        intArrayOf(
            Color.parseColor("#FF80AB"),
            Color.parseColor("#FF4081"),
            Color.parseColor("#F50057"),
            Color.parseColor("#C51162")
        ),
        intArrayOf(
            Color.parseColor("#EA80FC"),
            Color.parseColor("#E040FB"),
            Color.parseColor("#D500F9"),
            Color.parseColor("#AA00FF")
        ),
        intArrayOf(
            Color.parseColor("#B388FF"),
            Color.parseColor("#7C4DFF"),
            Color.parseColor("#651FFF"),
            Color.parseColor("#6200EA")
        ),
        intArrayOf(
            Color.parseColor("#8C9EFF"),
            Color.parseColor("#536DFE"),
            Color.parseColor("#3D5AFE"),
            Color.parseColor("#304FFE")
        ),
        intArrayOf(
            Color.parseColor("#82B1FF"),
            Color.parseColor("#448AFF"),
            Color.parseColor("#2979FF"),
            Color.parseColor("#2962FF")
        ),
        intArrayOf(
            Color.parseColor("#80D8FF"),
            Color.parseColor("#40C4FF"),
            Color.parseColor("#00B0FF"),
            Color.parseColor("#0091EA")
        ),
        intArrayOf(
            Color.parseColor("#84FFFF"),
            Color.parseColor("#18FFFF"),
            Color.parseColor("#00E5FF"),
            Color.parseColor("#00B8D4")
        ),
        intArrayOf(
            Color.parseColor("#A7FFEB"),
            Color.parseColor("#64FFDA"),
            Color.parseColor("#1DE9B6"),
            Color.parseColor("#00BFA5")
        ),
        intArrayOf(
            Color.parseColor("#B9F6CA"),
            Color.parseColor("#69F0AE"),
            Color.parseColor("#00E676"),
            Color.parseColor("#00C853")
        ),
        intArrayOf(
            Color.parseColor("#CCFF90"),
            Color.parseColor("#B2FF59"),
            Color.parseColor("#76FF03"),
            Color.parseColor("#64DD17")
        ),
        intArrayOf(
            Color.parseColor("#F4FF81"),
            Color.parseColor("#EEFF41"),
            Color.parseColor("#C6FF00"),
            Color.parseColor("#AEEA00")
        ),
        intArrayOf(
            Color.parseColor("#FFFF8D"),
            Color.parseColor("#FFFF00"),
            Color.parseColor("#FFEA00"),
            Color.parseColor("#FFD600")
        ),
        intArrayOf(
            Color.parseColor("#FFE57F"),
            Color.parseColor("#FFD740"),
            Color.parseColor("#FFC400"),
            Color.parseColor("#FFAB00")
        ),
        intArrayOf(
            Color.parseColor("#FFD180"),
            Color.parseColor("#FFAB40"),
            Color.parseColor("#FF9100"),
            Color.parseColor("#FF6D00")
        ),
        intArrayOf(
            Color.parseColor("#FF9E80"),
            Color.parseColor("#FF6E40"),
            Color.parseColor("#FF3D00"),
            Color.parseColor("#DD2C00")
        )
    )
}

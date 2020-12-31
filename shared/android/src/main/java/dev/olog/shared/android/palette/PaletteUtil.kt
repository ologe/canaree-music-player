package dev.olog.shared.android.palette

import android.graphics.Color

object PaletteUtil {

    private const val COLOR_INVALID = 1

    private const val LIGHTNESS_TEXT_DIFFERENCE_LIGHT = 20
    private const val LIGHTNESS_TEXT_DIFFERENCE_DARK = -10

    @JvmStatic
    fun ensureColors(background: Int, foreground: Int) : Triple<Int, Int, Int> {
        var primaryTextColor = COLOR_INVALID
        var secondaryTextColor = COLOR_INVALID

        if (primaryTextColor == COLOR_INVALID
                || secondaryTextColor == COLOR_INVALID
        ) {

            val backLum = ColorUtil.calculateLuminance(background)
            val textLum = ColorUtil.calculateLuminance(foreground)
            val contrast =
                ColorUtil.calculateContrast(foreground, background)
            // We only respect the given colors if worst case Black or White still has
            // contrast
            val backgroundLight = backLum > textLum && ColorUtil.satisfiesTextContrast(
                background,
                Color.BLACK
            ) || backLum <= textLum && !ColorUtil.satisfiesTextContrast(
                background,
                Color.WHITE
            )
            if (contrast < 4.5f) {
                if (backgroundLight) {
                    secondaryTextColor =
                        ColorUtil.findContrastColor(
                            foreground,
                            background,
                            true /* findFG */,
                            4.5
                        )
                    primaryTextColor =
                        ColorUtil.changeColorLightness(
                            secondaryTextColor,
                            -LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                        )
                } else {
                    secondaryTextColor =
                        ColorUtil.findContrastColorAgainstDark(
                            foreground,
                            background,
                            true /* findFG */,
                            4.5
                        )
                    primaryTextColor =
                        ColorUtil.changeColorLightness(
                            secondaryTextColor,
                            -LIGHTNESS_TEXT_DIFFERENCE_DARK
                        )
                }
            } else {
                primaryTextColor = foreground
                secondaryTextColor = ColorUtil.changeColorLightness(
                    primaryTextColor, if (backgroundLight)
                        LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                    else
                        LIGHTNESS_TEXT_DIFFERENCE_DARK
                )
                if (ColorUtil.calculateContrast(
                        secondaryTextColor,
                        background
                    ) < 4.5f) {
                    // oh well the secondary is not good enough
                    if (backgroundLight) {
                        secondaryTextColor =
                            ColorUtil.findContrastColor(
                                secondaryTextColor,
                                background,
                                true /* findFG */,
                                4.5
                            )
                    } else {
                        secondaryTextColor =
                            ColorUtil.findContrastColorAgainstDark(
                                secondaryTextColor,
                                background,
                                true /* findFG */,
                                4.5
                            )
                    }
                    primaryTextColor =
                        ColorUtil.changeColorLightness(
                            secondaryTextColor, if (backgroundLight)
                                -LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                            else
                                -LIGHTNESS_TEXT_DIFFERENCE_DARK
                        )
                }
            }
        }

        return Triple(background, primaryTextColor, secondaryTextColor)
    }

}
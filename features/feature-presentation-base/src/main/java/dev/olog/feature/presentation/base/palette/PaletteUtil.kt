package dev.olog.feature.presentation.base.palette

import android.graphics.Color

object PaletteUtil {

    private const val COLOR_INVALID = 1

    private const val LIGHTNESS_TEXT_DIFFERENCE_LIGHT = 20
    private const val LIGHTNESS_TEXT_DIFFERENCE_DARK = -10

    @JvmStatic
    fun ensureColors(background: Int, foreground: Int) : Triple<Int, Int, Int> {
        var primaryTextColor =
            COLOR_INVALID
        var secondaryTextColor =
            COLOR_INVALID

        if (primaryTextColor == COLOR_INVALID
                || secondaryTextColor == COLOR_INVALID
        ) {

            val backLum = dev.olog.feature.presentation.base.palette.ColorUtil.calculateLuminance(background)
            val textLum = dev.olog.feature.presentation.base.palette.ColorUtil.calculateLuminance(foreground)
            val contrast =
                dev.olog.feature.presentation.base.palette.ColorUtil.calculateContrast(foreground, background)
            // We only respect the given colors if worst case Black or White still has
            // contrast
            val backgroundLight = backLum > textLum && dev.olog.feature.presentation.base.palette.ColorUtil.satisfiesTextContrast(
                background,
                Color.BLACK
            ) || backLum <= textLum && !dev.olog.feature.presentation.base.palette.ColorUtil.satisfiesTextContrast(
                background,
                Color.WHITE
            )
            if (contrast < 4.5f) {
                if (backgroundLight) {
                    secondaryTextColor =
                        dev.olog.feature.presentation.base.palette.ColorUtil.findContrastColor(
                            foreground,
                            background,
                            true /* findFG */,
                            4.5
                        )
                    primaryTextColor =
                        dev.olog.feature.presentation.base.palette.ColorUtil.changeColorLightness(
                            secondaryTextColor,
                            -LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                        )
                } else {
                    secondaryTextColor =
                        dev.olog.feature.presentation.base.palette.ColorUtil.findContrastColorAgainstDark(
                            foreground,
                            background,
                            true /* findFG */,
                            4.5
                        )
                    primaryTextColor =
                        dev.olog.feature.presentation.base.palette.ColorUtil.changeColorLightness(
                            secondaryTextColor,
                            -LIGHTNESS_TEXT_DIFFERENCE_DARK
                        )
                }
            } else {
                primaryTextColor = foreground
                secondaryTextColor = dev.olog.feature.presentation.base.palette.ColorUtil.changeColorLightness(
                    primaryTextColor, if (backgroundLight)
                        LIGHTNESS_TEXT_DIFFERENCE_LIGHT
                    else
                        LIGHTNESS_TEXT_DIFFERENCE_DARK
                )
                if (dev.olog.feature.presentation.base.palette.ColorUtil.calculateContrast(
                        secondaryTextColor,
                        background
                    ) < 4.5f) {
                    // oh well the secondary is not good enough
                    if (backgroundLight) {
                        secondaryTextColor =
                            dev.olog.feature.presentation.base.palette.ColorUtil.findContrastColor(
                                secondaryTextColor,
                                background,
                                true /* findFG */,
                                4.5
                            )
                    } else {
                        secondaryTextColor =
                            dev.olog.feature.presentation.base.palette.ColorUtil.findContrastColorAgainstDark(
                                secondaryTextColor,
                                background,
                                true /* findFG */,
                                4.5
                            )
                    }
                    primaryTextColor =
                        dev.olog.feature.presentation.base.palette.ColorUtil.changeColorLightness(
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
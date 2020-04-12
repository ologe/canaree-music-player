package dev.olog.feature.presentation.base.widget

import android.content.Context
import android.graphics.Color
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.android.theme.PlayerAppearance

interface IColorDelegate {

    fun getDefaultColor(
        context: Context,
        playerAppearance: PlayerAppearance,
        isDarkMode: Boolean
    ): Int

    fun lightColor(): Int

}

object ColorDelegateImpl : IColorDelegate {

    override fun getDefaultColor(
        context: Context,
        playerAppearance: PlayerAppearance,
        isDarkMode: Boolean
    ): Int {
        return when {
            playerAppearance.isFullscreen || isDarkMode -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

    override fun lightColor(): Int {
        return 0xFF_F5F5F5.toInt()
    }
}
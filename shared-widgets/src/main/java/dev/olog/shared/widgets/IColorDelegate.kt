package dev.olog.shared.widgets

import android.content.Context
import android.graphics.Color
import dev.olog.platform.theme.HasPlayerAppearance
import dev.olog.ui.palette.colorControlNormal

interface IColorDelegate {

    fun getDefaultColor(
        context: Context,
        playerAppearance: HasPlayerAppearance,
        isDarkMode: Boolean
    ): Int

    fun lightColor(): Int

}

object ColorDelegateImpl : IColorDelegate {

    override fun getDefaultColor(
        context: Context,
        playerAppearance: HasPlayerAppearance,
        isDarkMode: Boolean
    ): Int {
        return when {
            playerAppearance.isFullscreen() || isDarkMode -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

    override fun lightColor(): Int {
        return 0xFF_F5F5F5.toInt()
    }
}
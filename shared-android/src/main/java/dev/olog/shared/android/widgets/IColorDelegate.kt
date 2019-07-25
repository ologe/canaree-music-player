package dev.olog.shared.android.widgets

import android.content.Context
import android.graphics.Color
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.android.theme.HasPlayerAppearance

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
            playerAppearance.isClean() && !isDarkMode -> 0xFF_8d91a6.toInt()
            playerAppearance.isFullscreen() || isDarkMode -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

    override fun lightColor(): Int {
        return 0xFF_F5F5F5.toInt()
    }
}
package dev.olog.shared.widgets

import android.content.Context
import android.graphics.Color
import dev.olog.shared.android.extensions.colorControlNormal
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.android.theme.playerAppearanceAmbient

interface IColorDelegate {

    fun getDefaultColor(context: Context): Int

    fun lightColor(): Int

}

object ColorDelegateImpl : IColorDelegate {

    override fun getDefaultColor(context: Context): Int {
        val playerAppearanceAmbient = context.playerAppearanceAmbient
        return when {
            playerAppearanceAmbient.isFullscreen() || context.isDarkMode -> Color.WHITE
            else -> context.colorControlNormal()
        }
    }

    override fun lightColor(): Int {
        return 0xFF_F5F5F5.toInt()
    }
}
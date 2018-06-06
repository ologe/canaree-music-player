package dev.olog.msc.presentation.widget

import android.graphics.Color
import android.view.View
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.isPortrait
import dev.olog.msc.utils.k.extension.textColorSecondary
import dev.olog.msc.utils.k.extension.textColorTertiary

interface DefaultTint {

    fun View.getDefaultColor(): Int {
        return when {
            context.isPortrait && AppTheme.isClean() -> 0xFF_929cb0.toInt()
            AppTheme.isFullscreen() -> Color.WHITE
            AppTheme.isDarkTheme() -> {
                alpha = .7f
                textColorSecondary()
            }
            else -> textColorTertiary()
        }
    }

}
package dev.olog.shared.compose

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import dev.olog.shared.compose.theme.CanareeColors
import dev.olog.shared.compose.theme.rememberColorScheme

@Composable
fun CanareeTheme(content: @Composable () -> Unit) {
    MaterialTheme(rememberColorScheme(Theme.colors.accentColor)) {
        CompositionLocalProvider(
            LocalDensity provides LinearFontScaleDensity(LocalDensity.current),
            LocalContentColor provides Theme.colors.textColorPrimary,
            LocalTextStyle provides LocalTextStyle.current.copy(
                letterSpacing = 0.01.em,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = true
                )
            )
        ) {
            content()
        }
    }
}

private class LinearFontScaleDensity(private val delegate: Density) : Density {

    override val density: Float = delegate.density
    override val fontScale: Float = 1f
}

object Theme {

    val colors: CanareeColors
        get() = CanareeColors

    val m3Colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

}
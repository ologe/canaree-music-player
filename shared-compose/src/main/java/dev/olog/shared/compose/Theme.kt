package dev.olog.shared.compose

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Density

@Composable
fun CanareeTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalDensity provides LinearFontScaleDensity(LocalDensity.current),
            LocalContentColor provides Theme.textColorPrimary,
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

    val textColorPrimary: Color
        @Composable
        get() = colorResource(id = R.color.textColorPrimary) // TODO this does not look right

    val textColorSecondary: Color
        @Composable
        get() = colorResource(id = R.color.textColorSecondary)

    val iconColor: Color
        @Composable
        get() = colorResource(id = R.color.colorControlNormal)

    val accentColor: Color
        @Composable
        get() = colorResource(id = R.color.defaultColorAccent) // TODO dynamic

}
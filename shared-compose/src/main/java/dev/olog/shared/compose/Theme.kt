package dev.olog.shared.compose

import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em

@Composable
fun CanareeTheme(content: @Composable () -> Unit) {
    MaterialTheme {
        CompositionLocalProvider(
            LocalDensity provides LinearFontScaleDensity(LocalDensity.current),
            LocalContentColor provides Theme.textColorPrimary,
            LocalTextStyle provides LocalTextStyle.current.copy(
                letterSpacing = 0.01.em,
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

    val background: Color
        @Composable
        get() = colorResource(R.color.colorBackground)

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
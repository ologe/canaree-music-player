package dev.olog.shared.compose.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.colorResource
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.compose.R

val LocalBackgroundColor = compositionLocalOf<Color> {
    error("LocalContentColor not set")
}

val LocalContentColor = compositionLocalOf<ColorSelector> {
    error("LocalContentColor not set")
}

internal val LocalCanareeColors = staticCompositionLocalOf<CanareeColors> {
    error("LocalCanareeColors not set")
}

@Immutable
data class CanareeColors(
    val background: Color,
    val iconColor: ColorSelector,
    val textColorPrimary: ColorSelector,
    val textColorSecondary: ColorSelector,
    val accent: Color,
    val onAccent: Color,
)

@Immutable
data class ColorSelector(
    val enabled: Color,
    val disabled: Color,
) {

    @Stable
    fun resolve(isEnabled: Boolean) = if (isEnabled) enabled else disabled
}

@Composable
internal fun colors(): CanareeColors {
    // TODO desaturate on dark mode
    val accentColor = if (LocalInspectionMode.current) {
        colorResource(R.color.defaultColorAccent)
    } else {
        // TODO check performance
        // TODO check that updates accordingly
        animateColorAsState(Color(LocalContext.current.colorAccent()), label = "accent color").value
    }

    return CanareeColors(
        background = colorResource(R.color.colorBackground),
        iconColor = ColorSelector(
            enabled = colorResource(R.color.colorControlNormal),
            disabled = colorResource(R.color.textColorPrimaryDisabled),
        ),
        textColorPrimary = ColorSelector(
            enabled = colorResource(R.color.textColorPrimary),
            disabled = colorResource(R.color.textColorPrimaryDisabled),
        ),
        textColorSecondary = ColorSelector(
            enabled = colorResource(R.color.textColorSecondary),
            disabled = colorResource(R.color.textColorSecondaryDisabled),
        ),
        accent = accentColor,
        // TODO use different colors from plan black and plain white?
        onAccent = if (accentColor.luminance() > .5) Color.Black else Color.White,
    )
}
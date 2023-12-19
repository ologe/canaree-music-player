package dev.olog.shared.compose.theme

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.android.material.color.utilities.Scheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews

val LocalBackgroundColor = compositionLocalOf<Color> { error("LocalBackgroundColor not set") }
val LocalContentColor = compositionLocalOf<ColorSelector> { error("LocalContentColor not set") }
val LocalIconColor = compositionLocalOf<ColorSelector> { error("LocalIconColor not set") }
internal val LocalCanareeColors =
    staticCompositionLocalOf<CanareeColors> { error("LocalCanareeColors not set") }

@Immutable
data class CanareeColors(
    val background: Color,
    val surface: Color,
    val iconColor: ColorSelector,
    val textColorPrimary: ColorSelector,
    val textColorSecondary: ColorSelector,
    val primary: ColorSelector,
    val onPrimary: ColorSelector,
    val secondary: ColorSelector,
    val onSecondary: ColorSelector,
) {

    @Composable
    fun textColor(isSelected: Boolean): ColorSelector {
        return if (isSelected) textColorPrimary else textColorSecondary
    }

}

@Immutable
data class ColorSelector(
    val enabled: Color,
    val disabled: Color,
) {

    @Stable
    fun resolve(isEnabled: Boolean) = if (isEnabled) enabled else disabled
}

@Suppress("AnimateAsStateLabel")
@SuppressLint("RestrictedApi")
@Composable
internal fun colors(
    darkMode: Boolean = isSystemInDarkTheme(),
): CanareeColors {
    val baseColor by rememberAccentColor()
    val scheme = remember(baseColor) {
        if (darkMode) Scheme.dark(baseColor.toArgb()) else Scheme.light(baseColor.toArgb())
    }

    // TODO add disabled version??
    val primaryColor = animateColorAsState(Color(scheme.primary)).value
    val onPrimaryColor = animateColorAsState(Color(scheme.onPrimary)).value
    val secondaryColor = animateColorAsState(Color(scheme.secondary)).value
    val onSecondaryColor = animateColorAsState(Color(scheme.onSecondary)).value

    return CanareeColors(
        background = animateColorAsState(Color(scheme.background)).value,
        surface = animateColorAsState(Color(scheme.surface)).value,
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
        primary = ColorSelector(primaryColor, primaryColor),
        onPrimary = ColorSelector(onPrimaryColor, onPrimaryColor),
        secondary = ColorSelector(secondaryColor, secondaryColor),
        onSecondary = ColorSelector(onSecondaryColor, onSecondaryColor),
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.background)
        ) {
            LazyVerticalGrid(GridCells.Fixed(4)) {
                item { ColorItem(colorResource(R.color.defaultColorAccent)) }
                item { ColorItem(Theme.colors.background) }
                item { ColorItem(Theme.colors.surface) }
                item { ColorItem(Theme.colors.iconColor.enabled) }
                item { ColorItem(Theme.colors.textColorPrimary.enabled) }
                item { ColorItem(Theme.colors.textColorSecondary.enabled) }
                item { ColorItem(Theme.colors.primary.enabled) }
                item { ColorItem(Theme.colors.onPrimary.enabled) }
                item { ColorItem(Theme.colors.secondary.enabled) }
                item { ColorItem(Theme.colors.onSecondary.enabled) }
            }
        }
    }
}

@Composable
private fun ColorItem(color: Color) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(color)
            .border(1.dp, Color.Black)
    )
}
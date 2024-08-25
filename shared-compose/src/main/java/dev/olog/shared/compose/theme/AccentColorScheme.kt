package dev.olog.shared.compose.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.MaterialDynamicColors
import com.google.android.material.color.utilities.SchemeTonalSpot
import dev.olog.shared.android.extensions.colorAccent
import dev.olog.shared.android.extensions.findInContextOrNull
import dev.olog.shared.android.extensions.isDarkMode
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme

@Composable
fun rememberColorScheme(accentColor: Color): ColorScheme {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        onDispose {
            context.findInContextOrNull<Activity>()?.window?.decorView?.setTag(R.id.color_scheme, null)
        }
    }
    return remember(context, accentColor) { context.colorSchemeM3 }
}

val Context.colorSchemeM3: ColorScheme
    get() = this.findInContextOrNull<Activity>()?.colorSchemeM3 ?: generateColorScheme()

val Activity.colorSchemeM3: ColorScheme
    get() {
        val scheme = window.decorView.getTag(R.id.color_scheme) as? ColorScheme
        if (scheme != null) return scheme
        return generateColorScheme().also {
            window.decorView.setTag(R.id.color_scheme, it)
        }
    }

val Fragment.colorSchemeM3: ColorScheme
    get() = requireActivity().colorSchemeM3

val ColorScheme.legacyAccent: Color
    get() = primary

@SuppressLint("RestrictedApi")
// if m3 hides SchemeTonalSpot, fork it from https://github.com/material-foundation/material-color-utilities/tree/main
private fun Context.generateColorScheme(
    fromColor: Color = Color(colorAccent()),
    isDarkMode: Boolean = isDarkMode(),
): ColorScheme {
    val scheme = SchemeTonalSpot(Hct.fromInt(fromColor.toArgb()), isDarkMode, 0.0)
    val materialDynamicColors = MaterialDynamicColors()
    return ColorScheme(
        primary = Color(materialDynamicColors.primary().getArgb(scheme)),
        onPrimary = Color(materialDynamicColors.onPrimary().getArgb(scheme)),
        primaryContainer = Color(materialDynamicColors.primaryContainer().getArgb(scheme)),
        onPrimaryContainer = Color(materialDynamicColors.onPrimaryContainer().getArgb(scheme)),
        inversePrimary = Color(materialDynamicColors.inversePrimary().getArgb(scheme)),
        secondary = Color(materialDynamicColors.secondary().getArgb(scheme)),
        onSecondary = Color(materialDynamicColors.onSecondary().getArgb(scheme)),
        secondaryContainer = Color(materialDynamicColors.secondaryContainer().getArgb(scheme)),
        onSecondaryContainer = Color(materialDynamicColors.onSecondaryContainer().getArgb(scheme)),
        tertiary = Color(materialDynamicColors.tertiary().getArgb(scheme)),
        onTertiary = Color(materialDynamicColors.onTertiary().getArgb(scheme)),
        tertiaryContainer = Color(materialDynamicColors.tertiaryContainer().getArgb(scheme)),
        onTertiaryContainer = Color(materialDynamicColors.onTertiaryContainer().getArgb(scheme)),
        background = Color(materialDynamicColors.background().getArgb(scheme)),
        onBackground = Color(materialDynamicColors.onBackground().getArgb(scheme)),
        surface = Color(materialDynamicColors.surface().getArgb(scheme)),
        onSurface = Color(materialDynamicColors.onSurface().getArgb(scheme)),
        surfaceVariant = Color(materialDynamicColors.surfaceVariant().getArgb(scheme)),
        onSurfaceVariant = Color(materialDynamicColors.onSurfaceVariant().getArgb(scheme)),
        surfaceTint = Color(materialDynamicColors.surfaceTint().getArgb(scheme)),
        inverseSurface = Color(materialDynamicColors.inverseSurface().getArgb(scheme)),
        inverseOnSurface = Color(materialDynamicColors.inverseOnSurface().getArgb(scheme)),
        error = Color(materialDynamicColors.error().getArgb(scheme)),
        onError = Color(materialDynamicColors.onError().getArgb(scheme)),
        errorContainer = Color(materialDynamicColors.errorContainer().getArgb(scheme)),
        onErrorContainer = Color(materialDynamicColors.onErrorContainer().getArgb(scheme)),
        outline = Color(materialDynamicColors.outline().getArgb(scheme)),
        outlineVariant = Color(materialDynamicColors.outlineVariant().getArgb(scheme)),
        scrim = Color(materialDynamicColors.scrim().getArgb(scheme)),
        surfaceBright = Color(materialDynamicColors.surfaceBright().getArgb(scheme)),
        surfaceDim = Color(materialDynamicColors.surfaceDim().getArgb(scheme)),
        surfaceContainer = Color(materialDynamicColors.surfaceContainer().getArgb(scheme)),
        surfaceContainerHigh = Color(materialDynamicColors.surfaceContainerHigh().getArgb(scheme)),
        surfaceContainerHighest = Color(materialDynamicColors.surfaceContainerHighest().getArgb(scheme)),
        surfaceContainerLow = Color(materialDynamicColors.surfaceContainerLow().getArgb(scheme)),
        surfaceContainerLowest = Color(materialDynamicColors.surfaceContainerLowest().getArgb(scheme)),
    )
}

@Preview
@Composable
private fun PreviewIndigo() {
    Shared(colorResource(R.color.md_indigo_A400))
}

@Preview
@Composable
private fun PreviewRed() {
    Shared(colorResource(R.color.md_red_A400))
}

@Composable
private fun Shared(accentColor: Color) {
    MaterialTheme(LocalContext.current.generateColorScheme(accentColor)) {
        Box(Modifier.background(Theme.colors.background)) {
            val colors = listOf(
                "primary" to Theme.m3Colors.primary,
                "onPrimary" to Theme.m3Colors.onPrimary,
                "primaryContainer" to Theme.m3Colors.primaryContainer,
                "onPrimaryContainer" to Theme.m3Colors.onPrimaryContainer,
                "inversePrimary" to Theme.m3Colors.inversePrimary,
                "secondary" to Theme.m3Colors.secondary,
                "onSecondary" to Theme.m3Colors.onSecondary,
                "secondaryContainer" to Theme.m3Colors.secondaryContainer,
                "onSecondaryContainer" to Theme.m3Colors.onSecondaryContainer,
                "tertiary" to Theme.m3Colors.tertiary,
                "onTertiary" to Theme.m3Colors.onTertiary,
                "tertiaryContainer" to Theme.m3Colors.tertiaryContainer,
                "onTertiaryContainer" to Theme.m3Colors.onTertiaryContainer,
                "background" to Theme.m3Colors.background,
                "onBackground" to Theme.m3Colors.onBackground,
                "surface" to Theme.m3Colors.surface,
                "onSurface" to Theme.m3Colors.onSurface,
                "surfaceVariant" to Theme.m3Colors.surfaceVariant,
                "onSurfaceVariant" to Theme.m3Colors.onSurfaceVariant,
                "surfaceTint" to Theme.m3Colors.surfaceTint,
                "inverseSurface" to Theme.m3Colors.inverseSurface,
                "inverseOnSurface" to Theme.m3Colors.inverseOnSurface,
                "error" to Theme.m3Colors.error,
                "onError" to Theme.m3Colors.onError,
                "errorContainer" to Theme.m3Colors.errorContainer,
                "onErrorContainer" to Theme.m3Colors.onErrorContainer,
                "outline" to Theme.m3Colors.outline,
                "outlineVariant" to Theme.m3Colors.outlineVariant,
                "scrim" to Theme.m3Colors.scrim,
                "surfaceBright" to Theme.m3Colors.surfaceBright,
                "surfaceDim" to Theme.m3Colors.surfaceDim,
                "surfaceContainer" to Theme.m3Colors.surfaceContainer,
                "surfaceContainerHigh" to Theme.m3Colors.surfaceContainerHigh,
                "surfaceContainerHighest" to Theme.m3Colors.surfaceContainerHighest,
                "surfaceContainerLow" to Theme.m3Colors.surfaceContainerLow,
                "surfaceContainerLowest" to Theme.m3Colors.surfaceContainerLowest,
            )

            LazyVerticalGrid(GridCells.Fixed(4)) {
                items(colors) { (name, color) ->
                    Spacer(
                        Modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .background(color)
                    )
                    Text(
                        name,
                        color = Theme.m3Colors.contentColorFor(color)
                    )
                }
            }
        }
    }
}
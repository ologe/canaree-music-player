package dev.olog.shared.compose.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.dp

@Composable
fun CanareeTheme(
    themeSettings: ThemeSettingsOverride? = null,
    content: @Composable () -> Unit
) {
    val colors = colors()
    val typography = typography()
    CompositionLocalProvider(
        LocalCanareeColors provides colors,
        LocalBackgroundColor provides colors.background,
        LocalContentColor provides colors.textColorPrimary,
        LocalCanareeTypography provides typography,
        LocalTextStyle provides typography.body,
        LocalCanareeSpacing provides spacing(),
        LocalScreenSpacing provides PaddingValues(horizontal = 8.dp), // TODO big screens?
        LocalIndication provides rememberRipple(),
    ) {
        ThemeSettings(themeSettings) {
            content()
        }
    }
}

object Theme {

    val colors: CanareeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCanareeColors.current

    val typography: CanareeTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCanareeTypography.current

    val spacing: CanareeSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalCanareeSpacing.current

}
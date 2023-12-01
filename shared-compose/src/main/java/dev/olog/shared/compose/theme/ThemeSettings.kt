package dev.olog.shared.compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.HasQuickAction
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.QuickAction

val LocalThemeSettings = staticCompositionLocalOf<ThemeSettings> {
    error("LocalThemeSettings not set")
}

@Stable
data class ThemeSettingsOverride(
    val imageShape: ImageShape? = null,
    val quickAction: QuickAction?  = null,
) {

    @Stable
    fun toThemeSettings(): ThemeSettings {
        return ThemeSettings(
            imageShape = imageShape ?: ThemeSettings.Default.imageShape,
            quickAction = quickAction ?: ThemeSettings.Default.quickAction,
        )
    }

}

@Stable
data class ThemeSettings(
    val imageShape: ImageShape,
    val quickAction: QuickAction,
) {

    companion object {
        val Default = ThemeSettings(
            imageShape = ImageShape.ROUND,
            quickAction = QuickAction.NONE,
        )
    }

}

@Composable
internal fun ThemeSettings(
    themeSettings: ThemeSettingsOverride? = null,
    content: @Composable () -> Unit,
) {
    val settings = when {
        themeSettings != null -> themeSettings.toThemeSettings()
        LocalInspectionMode.current -> ThemeSettings.Default
        else -> {
            val context = LocalContext.current.applicationContext
            // TODO collect collectAsStateWithLifecycle
            ThemeSettings(
                imageShape = context.findInContext<HasImageShape>()
                    .observeImageShape()
                    .collectAsState().value,
                quickAction = context.findInContext<HasQuickAction>()
                    .observeQuickAction()
                    .collectAsState().value
            )
        }
    }

    CompositionLocalProvider(
        LocalThemeSettings provides settings,
        content = content
    )
}
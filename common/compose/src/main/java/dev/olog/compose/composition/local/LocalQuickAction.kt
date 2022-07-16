package dev.olog.compose.composition.local

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.res.stringResource
import dev.olog.feature.settings.api.R
import dev.olog.platform.theme.QuickAction

val LocalQuickAction = staticCompositionLocalOf<QuickAction> { error("LocalQuickAction not set") }

@Composable
fun ProvideQuickActionPrefs(
    override: QuickAction? = null,
    content: @Composable () -> Unit
) {
    LocalPreference(
        key = stringResource(R.string.prefs_quick_action_key),
        serialize = { it.toPref(this) },
        deserialize = { mapValue(this, it) },
        default = QuickAction.NONE,
        override = override,
        providableCompositionLocal = LocalQuickAction,
        content = content,
    )
}

private fun mapValue(
    context: Context,
    value: String
): QuickAction {
    return when (value) {
        context.getString(dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
        context.getString(dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
        context.getString(dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_shuffle) -> QuickAction.SHUFFLE
        else -> QuickAction.NONE
    }
}

private fun QuickAction.toPref(context: Context): String {
    val key = when (this) {
        QuickAction.NONE -> dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_hide
        QuickAction.PLAY -> dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_play
        QuickAction.SHUFFLE -> dev.olog.feature.settings.api.R.string.prefs_quick_action_entry_value_shuffle
    }
    return context.getString(key)
}
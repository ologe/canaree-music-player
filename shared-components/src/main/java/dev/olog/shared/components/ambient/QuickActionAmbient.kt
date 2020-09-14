package dev.olog.shared.components.ambient

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import dev.olog.shared.components.R

val QuickActionAmbient = staticAmbientOf<QuickAction>()

@Composable
internal fun ProvideQuickActionAmbient(
    override: QuickAction? = null,
    content: @Composable () -> Unit
) {
    val context = ContextAmbient.current
    SharedPreferenceAmbient(
        key = stringResource(R.string.prefs_quick_action_key),
        default = stringResource(R.string.prefs_quick_action_entry_value_hide),
        override = override,
        mapper = { it.toIconShape(context) },
        content = {
            Providers(QuickActionAmbient provides it) {
                content()
            }
        }
    )

}

enum class QuickAction {
    NONE, PLAY, SHUFFLE
}

private fun String.toIconShape(context: Context): QuickAction = when (this) {
    context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
    context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
    context.getString(R.string.prefs_quick_action_entry_value_shuffle) -> QuickAction.SHUFFLE
    else -> {
        // TODO log and fallback to default
        QuickAction.NONE
    }
}
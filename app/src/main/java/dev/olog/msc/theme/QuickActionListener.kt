package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.android.theme.QuickAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class QuickActionListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<QuickAction>(
    context,
    prefs,
    context.getString(R.string.prefs_quick_action_key)
) {

    private val _flow by lazy { MutableStateFlow(getValue()) }
    val flow: StateFlow<QuickAction>
        get() = _flow

    override fun onPrefsChanged() {
        val quickActon = getValue()
        _flow.value = quickActon
    }

    override fun getValue(): QuickAction {
        val value =
            prefs.getString(key, context.getString(R.string.prefs_quick_action_entry_value_hide))


        return when (value) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
            else -> QuickAction.SHUFFLE
        }
    }
}


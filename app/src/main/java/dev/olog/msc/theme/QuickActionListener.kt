package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.msc.R
import dev.olog.shared.ConflatedSharedFlow
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.android.theme.QuickActionAmbient
import dev.olog.shared.value
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class QuickActionListener @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) : BaseThemeUpdater(
    key = context.getString(R.string.prefs_quick_action_key)
), QuickActionAmbient {

    private val _publisher = ConflatedSharedFlow(fetchValue())

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override val value: QuickAction
        get() = _publisher.value
    override val flow: Flow<QuickAction>
        get() = _publisher

    override fun onPrefsChanged() {
        _publisher.tryEmit(fetchValue())
    }

    private fun fetchValue(): QuickAction {
        val value = prefs.getString(key, context.getString(R.string.prefs_quick_action_entry_value_hide))

        return when (value) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
            else -> QuickAction.SHUFFLE
        }
    }
}


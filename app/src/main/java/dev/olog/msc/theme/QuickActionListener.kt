package dev.olog.msc.theme

import android.app.Application
import android.content.SharedPreferences
import dev.olog.presentation.R
import dev.olog.platform.theme.QuickAction
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject

internal class QuickActionListener @Inject constructor(
    application: Application,
    prefs: SharedPreferences
) : BaseThemeUpdater<QuickAction>(application, prefs, application.getString(R.string.prefs_quick_action_key)) {

    val quickActionPublisher by lazy { ConflatedBroadcastChannel(getValue()) }
    fun quickAction() = quickActionPublisher.value

    override fun onPrefsChanged() {

        val quickActon = getValue()
        quickActionPublisher.trySend(quickActon)
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


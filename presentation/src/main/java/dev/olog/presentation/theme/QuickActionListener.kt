package dev.olog.presentation.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.presentation.widgets.QuickActionView
import dev.olog.shared.CustomScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuickActionListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater(context, prefs, context.getString(R.string.prefs_quick_action_key)),
    CoroutineScope by CustomScope() {

    companion object {
        val quickActionPublisher = ConflatedBroadcastChannel<QuickActionView.Type>()
        fun quickAction() = quickActionPublisher.value
    }

    override fun onPrefsChanged(forced: Boolean) {
        val value = prefs.getString(key, context.getString(R.string.prefs_dark_mode_2_entry_value_follow_system))

        val quickActon = when (value) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickActionView.Type.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickActionView.Type.PLAY
            else -> QuickActionView.Type.SHUFFLE
        }
        GlobalScope.launch { quickActionPublisher.send(quickActon) }
    }

}


package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.presentation.R
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.theme.QuickAction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class QuickActionListener @Inject constructor(
        @ApplicationContext context: Context,
        prefs: SharedPreferences
) : BaseThemeUpdater(context, prefs, context.getString(R.string.prefs_quick_action_key)){

    val quickActionPublisher by lazyFast { ConflatedBroadcastChannel<QuickAction>() }
    fun quickAction() = quickActionPublisher.value

    override fun onPrefsChanged(forced: Boolean) {
        val value = prefs.getString(key, context.getString(R.string.prefs_quick_action_entry_value_hide))

        val quickActon = when (value) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickAction.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickAction.PLAY
            else -> QuickAction.SHUFFLE
        }
        GlobalScope.launch { quickActionPublisher.send(quickActon) }
    }

}


package dev.olog.shared.android.theme

import android.content.Context
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.Flow

interface HasQuickAction {
    fun getQuickAction(): QuickAction
    fun observeQuickAction(): Flow<QuickAction>
}

enum class QuickAction(@StringRes val prefValue: Int) {
    NONE(prefs.R.string.prefs_quick_action_entry_value_hide),
    PLAY(prefs.R.string.prefs_quick_action_entry_value_play),
    SHUFFLE(prefs.R.string.prefs_quick_action_entry_value_shuffle);

    companion object {
        fun fromPref(
            context: Context,
            value: String
        ): QuickAction {
            return values().find { context.getString(it.prefValue) == value } ?: NONE
        }
    }

}
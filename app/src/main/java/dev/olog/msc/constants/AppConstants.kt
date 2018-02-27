package dev.olog.msc.constants

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.QuickActionView

object AppConstants {

    private const val TAG = "AppConstants"
    const val ACTION_CONTENT_VIEW = TAG + ".action.content.view"

    const val SHORTCUT_SEARCH = TAG + ".shortcut.search"
    const val SHORTCUT_DETAIL = TAG + ".shortcut.detail"
    const val SHORTCUT_DETAIL_MEDIA_ID = TAG + ".shortcut.detail.media.id"

    lateinit var QUICK_ACTION: QuickActionView.Type

    const val PROGRESS_BAR_INTERVAL = 250

    const val UNKNOWN = "<unknown>"
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(context: Context){
        UNKNOWN_ALBUM = context.getString(R.string.common_unknown_album)
        UNKNOWN_ARTIST = context.getString(R.string.common_unknown_artist)

        QUICK_ACTION = getQuickAction(context)
    }

    fun updateQuickAction(context: Context){
        QUICK_ACTION = getQuickAction(context)
    }

    private fun getQuickAction(context: Context): QuickActionView.Type {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val quickAction = preferences.getString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        return when (quickAction) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickActionView.Type.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickActionView.Type.PLAY
            else ->  QuickActionView.Type.SHUFFLE
        }
    }


}
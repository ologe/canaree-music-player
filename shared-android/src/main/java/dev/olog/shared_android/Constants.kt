package dev.olog.shared_android

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.shared_android.entity.QuickAction
import dev.olog.shared_android.entity.QuickActionEnum

object Constants {

    var quickAction = QuickAction(QuickActionEnum.NONE)

    const val LAST_ADDED_ID: Long = -3000
    const val FAVORITE_LIST_ID: Long = -3002
    const val HISTORY_LIST_ID: Long = -3004

    val autoPlaylists = listOf(
            LAST_ADDED_ID, FAVORITE_LIST_ID, HISTORY_LIST_ID
    )


    const val UNKNOWN = "<unknown>"
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(context: Context){
        UNKNOWN_ALBUM = context.getString(R.string.unknown_album)
        UNKNOWN_ARTIST = context.getString(R.string.unknown_artist)

        quickAction = getQuickAction(context)
    }

    fun updateQuickAction(context: Context){
        quickAction = getQuickAction(context)
    }

    private fun getQuickAction(context: Context): QuickAction {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val quickAction = preferences.getString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_hide))
        val enum = when (quickAction) {
            context.getString(R.string.prefs_quick_action_hide) -> QuickActionEnum.NONE
            context.getString(R.string.prefs_quick_action_play) -> QuickActionEnum.PLAY
            else ->  QuickActionEnum.SHUFFLE
        }
        return QuickAction(enum)
    }


}
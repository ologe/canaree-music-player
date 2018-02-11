package dev.olog.msc.constants

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.shared_android.entity.QuickAction
import dev.olog.shared_android.entity.QuickActionEnum

object Constants {

    const val ACTION_CONTENT_VIEW = "ACTION_CONTENT_VIEW"

    const val SHORTCUT_SEARCH = "SHORTCUT_SEARCH"
    const val SHORTCUT_PLAY = "SHORTCUT_PLAY"
    const val SHORTCUT_SHUFFLE = "SHORTCUT_SHUFFLE"
    const val WIDGET_ACTION_PLAY_PAUSE = "WIDGET_ACTION_PLAY"
    const val WIDGET_ACTION_SKIP_NEXT = "WIDGET_ACTION_SKIP_NEXT"
    const val WIDGET_ACTION_SKIP_PREVIOUS = "WIDGET_ACTION_SKIP_PREVIOUS"

    var quickAction = QuickAction(QuickActionEnum.NONE)
    var useNeuralImages: Boolean = false

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
        useNeuralImages = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.prefs_use_neural_images_key), false)
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
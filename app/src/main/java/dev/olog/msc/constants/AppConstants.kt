package dev.olog.msc.constants

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.QuickActionView

object AppConstants {

    private const val TAG = "AppConstants"
    const val ACTION_CONTENT_VIEW = TAG + ".action.content.view"

    const val SHORTCUT_SEARCH = TAG + ".shortcut.search"

    lateinit var QUICK_ACTION: QuickActionView.Type
    var useNeuralImages: Boolean = false

    const val UNKNOWN = "<unknown>"
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(context: Context){
        UNKNOWN_ALBUM = context.getString(R.string.unknown_album)
        UNKNOWN_ARTIST = context.getString(R.string.unknown_artist)

        QUICK_ACTION = getQuickAction(context)
        useNeuralImages = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.prefs_use_neural_images_key), false)
    }

    fun updateQuickAction(context: Context){
        QUICK_ACTION = getQuickAction(context)
    }

    private fun getQuickAction(context: Context): QuickActionView.Type {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val quickAction = preferences.getString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_hide))
        return when (quickAction) {
            context.getString(R.string.prefs_quick_action_hide) -> QuickActionView.Type.NONE
            context.getString(R.string.prefs_quick_action_play) -> QuickActionView.Type.PLAY
            else ->  QuickActionView.Type.SHUFFLE
        }
    }


}
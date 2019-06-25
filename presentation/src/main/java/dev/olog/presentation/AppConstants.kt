package dev.olog.presentation

import android.content.Context
import android.provider.MediaStore
import androidx.preference.PreferenceManager

object AppConstants {

    private const val TAG = "AppConstants"
    const val ACTION_CONTENT_VIEW = "$TAG.action.content.view"

    const val NO_IMAGE = "NO_IMAGE"

    var SHOW_LOCKSCREEN_IMAGE = false

    const val PROGRESS_BAR_INTERVAL = 250

    const val UNKNOWN = MediaStore.UNKNOWN_STRING
    lateinit var UNKNOWN_ALBUM: String
    lateinit var UNKNOWN_ARTIST: String

    fun initialize(context: Context){
        UNKNOWN_ALBUM = context.getString(R.string.common_unknown_album)
        UNKNOWN_ARTIST = context.getString(R.string.common_unknown_artist)

        updateLockscreenArtworkEnabled(context)
    }

    fun updateLockscreenArtworkEnabled(context: Context) {
        SHOW_LOCKSCREEN_IMAGE =
            getLockscreenArtworkEnabled(context)
    }

    private fun getLockscreenArtworkEnabled(context: Context): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return preferences.getBoolean(context.getString(R.string.prefs_lockscreen_artwork_key), false)
    }

}
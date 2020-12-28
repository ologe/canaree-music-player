package dev.olog.data.local.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.olog.core.prefs.TutorialPreferenceGateway
import javax.inject.Inject

internal class TutorialPreferenceGatewayImpl @Inject constructor(
    private val preferences: SharedPreferences
) : TutorialPreferenceGateway {

    companion object {
        private const val TAG = "TutorialPreferenceImpl"
        private const val SORT_BY_SHOWN = "$TAG.SORT_BY_SHOWN"
        private const val FLOATING_WINDOW_SHOWN = "$TAG.FLOATING_WINDOW_SHOWN"
        private const val LYRICS_SHOWN = "$TAG.LYRICS_SHOWN"
        private const val ADD_LYRICS_SHOWN = "$TAG.ADD_LYRICS_SHOWN_2"
    }

    override fun sortByTutorial(): Boolean {
        val alreadyShown = preferences.getBoolean(SORT_BY_SHOWN, false)
        if (!alreadyShown){
            disableSortByTutorial()
        }
        return !alreadyShown
    }

    override fun floatingWindowTutorial(): Boolean {
        val alreadyShown = preferences.getBoolean(FLOATING_WINDOW_SHOWN, false)
        if (!alreadyShown){
            disableFloatingWindowTutorial()
        }
        return !alreadyShown
    }

    override fun lyricsTutorial(): Boolean {
        val alreadyShown = preferences.getBoolean(LYRICS_SHOWN, false)
        if (!alreadyShown){
            disableLyricsTutorial()
        }
        return !alreadyShown
    }

    override fun editLyrics(): Boolean {
        val alreadyShown = preferences.getBoolean(ADD_LYRICS_SHOWN, false)
        if (!alreadyShown){
            disableAddLyricsTutorial()
        }
        return !alreadyShown
    }

    private fun disableSortByTutorial(){
        preferences.edit { putBoolean(SORT_BY_SHOWN, true) }
    }

    private fun disableFloatingWindowTutorial(){
        preferences.edit { putBoolean(FLOATING_WINDOW_SHOWN, true) }
    }

    private fun disableLyricsTutorial(){
        preferences.edit { putBoolean(LYRICS_SHOWN, true) }
    }

    private fun disableAddLyricsTutorial(){
        preferences.edit { putBoolean(ADD_LYRICS_SHOWN, true) }
    }

    // TODO reset all??
    override fun reset() {
        preferences.edit {
            putBoolean(SORT_BY_SHOWN, false)
            putBoolean(FLOATING_WINDOW_SHOWN, false)
            putBoolean(LYRICS_SHOWN, false)
            putBoolean(ADD_LYRICS_SHOWN, false)
        }
    }

}
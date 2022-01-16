package dev.olog.core.prefs

import dev.olog.core.preference.Prefs

interface TutorialPreferenceGateway : Prefs {

    fun sortByTutorial(): Boolean
    fun floatingWindowTutorial(): Boolean
    fun lyricsTutorial(): Boolean
    fun editLyrics(): Boolean
    fun reset()

}
package dev.olog.domain.prefs

interface TutorialPreferenceGateway {

    fun sortByTutorial(): Boolean
    fun floatingWindowTutorial(): Boolean
    fun lyricsTutorial(): Boolean
    fun editLyrics(): Boolean
    fun reset()

}
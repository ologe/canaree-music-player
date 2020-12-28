package dev.olog.core.prefs

import dev.olog.core.ResettablePreference

interface TutorialPreferenceGateway : ResettablePreference {

    fun sortByTutorial(): Boolean
    fun floatingWindowTutorial(): Boolean
    fun lyricsTutorial(): Boolean
    fun editLyrics(): Boolean

}
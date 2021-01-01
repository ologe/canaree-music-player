package dev.olog.domain.prefs

import dev.olog.domain.ResettablePreference

interface TutorialPreferenceGateway : ResettablePreference {

    fun sortByTutorial(): Boolean
    fun floatingWindowTutorial(): Boolean
    fun lyricsTutorial(): Boolean
    fun editLyrics(): Boolean

}
package dev.olog.core.prefs

import io.reactivex.Completable

interface TutorialPreferenceGateway {

    fun sortByTutorial(): Completable
    fun floatingWindowTutorial(): Completable
    fun lyricsTutorial(): Completable
    fun editLyrics(): Completable
    fun reset()

}
package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TutorialPreferenceUseCase @Inject constructor(
        private val gateway: TutorialPreferenceGateway
) {

    fun sortByTutorial(): Completable = gateway.sortByTutorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun floatingWindowTutorial(): Completable = gateway.floatingWindowTutorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun lyricsTutorial(): Completable = gateway.lyricsTutorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun addLyricsTutorial(): Completable = gateway.editLyrics()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun reset() {
        gateway.reset()
    }

}
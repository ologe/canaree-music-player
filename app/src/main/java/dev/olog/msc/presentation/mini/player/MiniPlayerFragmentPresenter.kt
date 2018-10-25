package dev.olog.msc.presentation.mini.player

import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.utils.k.extension.asLiveData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MiniPlayerFragmentPresenter @Inject constructor(
        private val musicPrefsUseCase: MusicPreferencesUseCase

)  {

    private var showTimeLeft = false
    private var currentDuration = 0L
    private val progressPublisher = BehaviorSubject.createDefault(0L)

    val skipToNextVisibility = musicPrefsUseCase
            .observeSkipToNextVisibility()
            .asLiveData()

    val skipToPreviousVisibility = musicPrefsUseCase
            .observeSkipToPreviousVisibility()
            .asLiveData()

    fun getMetadata() = musicPrefsUseCase.getLastMetadata()

    fun startShowingLeftTime(show: Boolean, duration: Long){
        showTimeLeft = show
        currentDuration = duration
    }

    val observeProgress : Observable<Long> = progressPublisher
            .observeOn(Schedulers.computation())
            .filter { showTimeLeft }
            .map { currentDuration - progressPublisher.value!! }
            .map { TimeUnit.MILLISECONDS.toMinutes(it) }

    fun updateProgress(progress: Long){
        progressPublisher.onNext(progress)
    }

}
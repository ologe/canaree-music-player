package dev.olog.msc.presentation.offline.lyrics

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.OfflineLyrics
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.prefs.TutorialPreferenceUseCase
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
        private val observeUseCase: ObserveOfflineLyricsUseCase,
        private val insertUseCase: InsertOfflineLyricsUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceUseCase
) {

    private var lyricsDisposable: Disposable? = null

    private val currentTrackIdPublisher = BehaviorSubject.create<Long>()

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentTrackId(trackId: Long){
        currentTrackIdPublisher.onNext(trackId)
    }

    fun updateCurrentMetadata(title: String, artist: String){
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun observeLyrics(): Observable<String> {
        return currentTrackIdPublisher.switchMap { id ->
            observeUseCase.execute(id)
        }
    }

    fun updateLyrics(lyrics: String){
        lyricsDisposable.unsubscribe()
        lyricsDisposable = insertUseCase.execute(OfflineLyrics(currentTrackIdPublisher.value ?: -1, lyrics))
                .subscribe({}, Throwable::printStackTrace)
    }

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != AppConstants.UNKNOWN_ARTIST){
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun onStop(){
        lyricsDisposable.unsubscribe()
    }

    fun showAddLyricsIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.addLyricsTutorial()
    }

}
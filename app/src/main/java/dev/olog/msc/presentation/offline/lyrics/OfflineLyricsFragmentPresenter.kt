package dev.olog.msc.presentation.offline.lyrics

import dev.olog.presentation.AppConstants
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.domain.interactor.offline.lyrics.InsertOfflineLyricsUseCase
import dev.olog.msc.domain.interactor.offline.lyrics.ObserveOfflineLyricsUseCase
import dev.olog.msc.offline.lyrics.BaseOfflineLyricsPresenter
import io.reactivex.Completable
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
        observeUseCase: ObserveOfflineLyricsUseCase,
        insertUseCase: InsertOfflineLyricsUseCase,
        private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
        appPreferencesUseCase: AppPreferencesGateway

) : BaseOfflineLyricsPresenter(appPreferencesUseCase, observeUseCase, insertUseCase) {

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentMetadata(title: String, artist: String){
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != AppConstants.UNKNOWN_ARTIST){
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun showAddLyricsIfNeverShown(): Completable {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

}
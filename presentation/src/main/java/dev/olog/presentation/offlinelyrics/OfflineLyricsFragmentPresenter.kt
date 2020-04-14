package dev.olog.presentation.offlinelyrics

import android.provider.MediaStore
import dev.olog.domain.gateway.OfflineLyricsGateway
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.offline.lyrics.BaseOfflineLyricsPresenter
import dev.olog.lib.offline.lyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.lib.offline.lyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
    observeUseCase: ObserveOfflineLyricsUseCase,
    insertUseCase: InsertOfflineLyricsUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    lyricsGateway: OfflineLyricsGateway,
    schedulers: Schedulers

) : BaseOfflineLyricsPresenter(
    lyricsGateway,
    observeUseCase,
    insertUseCase,
    schedulers
) {

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentMetadata(title: String, artist: String) {
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != MediaStore.UNKNOWN_STRING) {
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun showAddLyricsIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

}
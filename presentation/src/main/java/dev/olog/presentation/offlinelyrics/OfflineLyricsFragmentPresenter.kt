package dev.olog.presentation.offlinelyrics

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.intents.AppConstants
import dev.olog.lib.offline.lyrics.BaseOfflineLyricsPresenter
import dev.olog.lib.offline.lyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.lib.offline.lyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

class OfflineLyricsFragmentPresenter @Inject constructor(
    @ApplicationContext context: Context,
    observeUseCase: ObserveOfflineLyricsUseCase,
    insertUseCase: InsertOfflineLyricsUseCase,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    lyricsGateway: OfflineLyricsGateway

) : BaseOfflineLyricsPresenter(context, lyricsGateway, observeUseCase, insertUseCase) {

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    fun updateCurrentMetadata(title: String, artist: String) {
        this.currentTitle = title
        this.currentArtist = artist
    }

    fun getInfoMetadata(): String {
        var result = currentTitle
        if (currentArtist != AppConstants.UNKNOWN) {
            result += " $currentArtist"
        }
        result += " lyrics"
        return result
    }

    fun showAddLyricsIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.lyricsTutorial()
    }

}
package dev.olog.feature.offline.lyrics

import android.content.Context
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.OfflineLyricsGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.lib.offline.lyrics.BaseOfflineLyricsPresenter
import dev.olog.lib.offline.lyrics.domain.InsertOfflineLyricsUseCase
import dev.olog.lib.offline.lyrics.domain.ObserveOfflineLyricsUseCase
import javax.inject.Inject

internal class OfflineLyricsFragmentPresenter @Inject constructor(
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
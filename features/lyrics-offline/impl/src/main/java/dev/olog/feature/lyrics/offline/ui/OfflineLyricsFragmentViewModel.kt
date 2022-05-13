package dev.olog.feature.lyrics.offline.ui

import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.lyrics.offline.LyricsOfflinePresenter
import javax.inject.Inject

@HiltViewModel
class OfflineLyricsFragmentViewModel @Inject constructor(
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    delegate: LyricsOfflinePresenter,
) : ViewModel(), LyricsOfflinePresenter by delegate {

    private var currentTitle: String = ""
    private var currentArtist: String = ""

    init {
        onStart()
    }

    override fun onCleared() {
        super.onCleared()
        onStop()
    }

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
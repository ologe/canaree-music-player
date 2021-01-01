package dev.olog.feature.player.volume

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.domain.prefs.MusicPreferencesGateway

internal class PlayerVolumeFragmentViewModel @ViewModelInject constructor(
    private val musicPrefs: MusicPreferencesGateway
) : ViewModel() {

    var volume: Int
        get() = musicPrefs.volume
        set(value) {
            musicPrefs.volume = value
        }


}
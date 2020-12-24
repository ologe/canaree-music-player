package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.lib.media.model.PlayerShuffleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@ServiceScoped
internal class MusicServiceShuffleMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway
) {

    private val _state = MutableStateFlow(
        PlayerShuffleMode.of(musicPreferencesUseCase.getShuffleMode())
    )
    val state: Flow<PlayerShuffleMode>
        get() = _state.filterNotNull()

    private val value: PlayerShuffleMode
        get() = _state.value

    fun isEnabled(): Boolean = value == PlayerShuffleMode.ENABLED

    fun setEnabled(value: PlayerShuffleMode) {
        val platformValue = value.toPlatform()
        _state.value = value
        musicPreferencesUseCase.setShuffleMode(platformValue)
        mediaSession.setShuffleMode(platformValue)
    }

    fun toggle() {
        setEnabled(_state.value.cycled())
    }

}

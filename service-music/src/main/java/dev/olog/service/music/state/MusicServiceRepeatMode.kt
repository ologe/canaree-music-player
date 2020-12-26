package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.lib.media.model.PlayerRepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@ServiceScoped
internal class MusicServiceRepeatMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway

) {

    private val _state = MutableStateFlow(
        PlayerRepeatMode.of(musicPreferencesUseCase.getRepeatMode())
    )

    val state: Flow<PlayerRepeatMode>
        get() = _state.filterNotNull()

    private val value: PlayerRepeatMode
        get() = _state.value

    init {
        mediaSession.setRepeatMode(_state.value.toPlatform())
    }

    fun isRepeatOne(): Boolean = value == PlayerRepeatMode.ONE

    fun isRepeatAll(): Boolean = value == PlayerRepeatMode.ALL

    fun toggle() {
        val value = _state.value.cycled()
        val platformValue = value.toPlatform()
        _state.value = value
        musicPreferencesUseCase.setRepeatMode(platformValue)
        mediaSession.setRepeatMode(platformValue)
    }

}

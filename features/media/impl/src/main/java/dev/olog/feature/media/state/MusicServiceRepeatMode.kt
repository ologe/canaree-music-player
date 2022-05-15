package dev.olog.feature.media.state

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_INVALID
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.feature.media.api.MusicPreferencesGateway
import javax.inject.Inject
import kotlin.properties.Delegates

@ServiceScoped
internal class MusicServiceRepeatMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway

) {

    private var state by Delegates.observable(REPEAT_MODE_INVALID) { _, _, new ->
        musicPreferencesUseCase.setRepeatMode(new)
        mediaSession.setRepeatMode(new)
    }

    init {
        state = musicPreferencesUseCase.getRepeatMode()
    }

    fun isRepeatNone(): Boolean = state == REPEAT_MODE_NONE

    fun isRepeatOne(): Boolean = state == REPEAT_MODE_ONE

    fun isRepeatAll(): Boolean = state == REPEAT_MODE_ALL

    fun update() {
        val oldState = state

        this.state = when (oldState) {
            REPEAT_MODE_NONE -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            else -> REPEAT_MODE_NONE
        }

    }

}

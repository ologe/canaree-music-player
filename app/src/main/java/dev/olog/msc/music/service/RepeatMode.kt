package dev.olog.msc.music.service

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import dev.olog.msc.dagger.scope.PerService
import dev.olog.core.prefs.MusicPreferencesGateway
import javax.inject.Inject


@PerService
class RepeatMode @Inject constructor(
        private val mediaSession: MediaSessionCompat,
        private val musicPreferencesUseCase: MusicPreferencesGateway

) {

    init {
        mediaSession.setRepeatMode(getState())
    }

    fun getState(): Int = musicPreferencesUseCase.getRepeatMode()

    fun isRepeatNone(): Boolean = getState() == REPEAT_MODE_NONE

    fun isRepeatOne(): Boolean = getState() == PlaybackStateCompat.REPEAT_MODE_ONE

    fun isRepeatAll(): Boolean = getState() == REPEAT_MODE_ALL

    fun update() {
        val repeatMode = getState()

        val newState = when (repeatMode){
            REPEAT_MODE_NONE -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            else -> REPEAT_MODE_NONE
        }

        musicPreferencesUseCase.setRepeatMode(newState)
        mediaSession.setRepeatMode(newState)
    }

}

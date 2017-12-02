package dev.olog.music_service

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import dev.olog.domain.interactor.service.RepeatModeUseCase
import dev.olog.music_service.di.PerService
import javax.inject.Inject


@PerService
class RepeatMode @Inject constructor(
        private val mediaSession: MediaSessionCompat,
        private val repeatModeUseCase: RepeatModeUseCase) {

    init {
        mediaSession.setRepeatMode(getState())
    }

    private fun getState(): Int = repeatModeUseCase.get()

    fun isRepeatNone(): Boolean = getState() == REPEAT_MODE_NONE

    fun isRepeatOne(): Boolean = getState() == PlaybackStateCompat.REPEAT_MODE_ONE

    fun isRepeatAll(): Boolean = getState() == REPEAT_MODE_ALL

    fun update() {
        val repeatMode = repeatModeUseCase.get()

        val newState: Int

        newState = when (repeatMode){
            REPEAT_MODE_NONE -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            else -> REPEAT_MODE_NONE
        }

        repeatModeUseCase.set(newState)
        mediaSession.setRepeatMode(newState)
    }

}

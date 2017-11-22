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

    private val state: Int
        get() = repeatModeUseCase.get()

    fun isRepeatNone(): Boolean = state == REPEAT_MODE_NONE

    fun isRepeatOne(): Boolean = state == PlaybackStateCompat.REPEAT_MODE_ONE

    val isRepeatAll: Boolean = state == REPEAT_MODE_ALL

    init {
        mediaSession.setRepeatMode(state)
    }

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

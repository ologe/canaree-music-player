package dev.olog.music_service

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import dev.olog.domain.interactor.music_service.ShuffleModeUseCase
import dev.olog.music_service.di.PerService
import javax.inject.Inject

@PerService
class ShuffleMode @Inject constructor(
        private val mediaSession: MediaSessionCompat,
        private val shuffleModeUseCase: ShuffleModeUseCase) {

    init {
        mediaSession.setShuffleMode(getState())
    }

    fun isEnabled(): Boolean= getState() != SHUFFLE_MODE_NONE

    fun setEnabled(enabled: Boolean){
        val shuffleMode = if (enabled) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE
        shuffleModeUseCase.set(shuffleMode)
        mediaSession.setShuffleMode(shuffleMode)
    }

    fun getState(): Int = shuffleModeUseCase.get()

    /**
     * @return true if new shuffle state is enabled
     */
    fun update(): Boolean {
        val shuffleMode = getState()

        val newState: Int

        if (shuffleMode == SHUFFLE_MODE_NONE) {
            newState = SHUFFLE_MODE_ALL
        } else {
            newState = SHUFFLE_MODE_NONE
        }

        shuffleModeUseCase.set(newState)
        mediaSession.setShuffleMode(newState)

        return newState != SHUFFLE_MODE_NONE
    }

}

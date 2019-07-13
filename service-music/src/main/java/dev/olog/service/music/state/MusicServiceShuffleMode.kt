package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_ALL
import android.support.v4.media.session.PlaybackStateCompat.SHUFFLE_MODE_NONE
import android.util.Log
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.injection.dagger.PerService
import javax.inject.Inject

@PerService
internal class MusicServiceShuffleMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway
) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${this::class.java.simpleName}"
    }

    init {
        val state = getState()
        mediaSession.setShuffleMode(state)
        Log.v(TAG, "setup state=$state")
    }

    fun isEnabled(): Boolean = getState() != SHUFFLE_MODE_NONE

    fun setEnabled(enabled: Boolean) {
        Log.v(TAG, "set enabled=$enabled")
        val shuffleMode = if (enabled) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE
        musicPreferencesUseCase.setShuffleMode(shuffleMode)
        mediaSession.setShuffleMode(shuffleMode)
    }

    fun getState(): Int = musicPreferencesUseCase.getShuffleMode()

    /**
     * @return true if new shuffle state is enabled
     */
    fun update(): Boolean {
        val oldState = getState()

        val newState = if (oldState == SHUFFLE_MODE_NONE) {
            SHUFFLE_MODE_ALL
        } else {
            SHUFFLE_MODE_NONE
        }

        musicPreferencesUseCase.setShuffleMode(newState)
        mediaSession.setShuffleMode(newState)

        Log.v(TAG, "update old state=$oldState, new state=$newState")

        return newState != SHUFFLE_MODE_NONE
    }

}

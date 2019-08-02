package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.injection.dagger.PerService
import javax.inject.Inject

@PerService
internal class MusicServiceRepeatMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway

) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicServiceRepeatMode::class.java.simpleName}"
    }

    init {
        val state = getState() // TODO blocking operation
        mediaSession.setRepeatMode(state)
        Log.v(TAG, "setup state=$state")
    }

    fun getState(): Int = musicPreferencesUseCase.getRepeatMode()

    // TODO blocking io call, i think is the same in shuffle
    fun isRepeatNone(): Boolean = getState() == REPEAT_MODE_NONE

    fun isRepeatOne(): Boolean = getState() == REPEAT_MODE_ONE

    fun isRepeatAll(): Boolean = getState() == REPEAT_MODE_ALL

    fun update() {
        val oldState = getState()

        val newState = when (oldState) {
            REPEAT_MODE_NONE -> REPEAT_MODE_ALL
            REPEAT_MODE_ALL -> REPEAT_MODE_ONE
            else -> REPEAT_MODE_NONE
        }

        musicPreferencesUseCase.setRepeatMode(newState)
        mediaSession.setRepeatMode(newState)

        Log.v(TAG, "update old state=$oldState, new state=$newState")
    }

}

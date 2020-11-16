package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.core.prefs.MusicPreferencesGateway
import javax.inject.Inject
import kotlin.properties.Delegates

@ServiceScoped
internal class MusicServiceShuffleMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway
) {

    private var state by Delegates.observable(SHUFFLE_MODE_INVALID) { _, _, new ->
        musicPreferencesUseCase.setShuffleMode(new)
        mediaSession.setShuffleMode(new)
    }

    init {
        this.state = musicPreferencesUseCase.getShuffleMode()
    }

    fun isEnabled(): Boolean = state != SHUFFLE_MODE_NONE

    fun setEnabled(enabled: Boolean) {
        this.state = if (enabled) SHUFFLE_MODE_ALL else SHUFFLE_MODE_NONE
    }

    /**
     * @return true if new shuffle state is enabled
     */
    fun update(): Boolean {
        val oldState = state

        this.state = if (oldState == SHUFFLE_MODE_NONE) {
            SHUFFLE_MODE_ALL
        } else {
            SHUFFLE_MODE_NONE
        }

        return this.state != SHUFFLE_MODE_NONE
    }

}

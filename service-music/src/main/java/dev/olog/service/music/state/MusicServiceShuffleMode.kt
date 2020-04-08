package dev.olog.service.music.state

import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import dev.olog.core.dagger.FeatureScope
import dev.olog.domain.prefs.MusicPreferencesGateway
import timber.log.Timber
import javax.inject.Inject
import kotlin.properties.Delegates

@FeatureScope
internal class MusicServiceShuffleMode @Inject constructor(
    private val mediaSession: MediaSessionCompat,
    private val musicPreferencesUseCase: MusicPreferencesGateway
) {

    companion object {
        @JvmStatic
        private val TAG = "SM:${MusicServiceShuffleMode::class.java.simpleName}"
    }

    private var state by Delegates.observable(SHUFFLE_MODE_INVALID) { _, _, new ->
        musicPreferencesUseCase.setShuffleMode(new)
        mediaSession.setShuffleMode(new)
    }

    init {
        this.state = musicPreferencesUseCase.getShuffleMode()
        Timber.v("$TAG setup state=$state")
    }

    fun isEnabled(): Boolean = state != SHUFFLE_MODE_NONE

    fun setEnabled(enabled: Boolean) {
        Timber.v("$TAG set enabled=$enabled")
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

        Timber.v("$TAG update old state=$oldState, new state=${this.state}")

        return this.state != SHUFFLE_MODE_NONE
    }

}

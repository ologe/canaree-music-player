package dev.olog.service.music.focus

import android.content.Context
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import dagger.Lazy
import dev.olog.injection.dagger.ServiceContext
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.Player
import dev.olog.service.music.model.FocusState
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.utils.assertMainThread
import javax.inject.Inject

internal class AudioFocusBehavior @Inject constructor(
    @ServiceContext context: Context,
    private val player: Lazy<Player>, // keep it lazy to avoid circular dependency
    private val volume: IMaxAllowedPlayerVolume

) : AudioManager.OnAudioFocusChangeListener {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val focusRequest by lazyFast { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    fun requestFocus(): Boolean {
        assertMainThread()

        val focus = requestFocusInternal()
        currentFocus = when (focus) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> FocusState.GAIN
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> FocusState.DELAYED
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> FocusState.NONE
            else -> throw IllegalStateException("audio focus response not handle with code $focus")
        }
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonFocus() {
        assertMainThread()

        currentFocus = FocusState.NONE
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
    }

    private fun requestFocusInternal(): Int {
        return AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
    }

    private fun buildFocusRequest(): AudioFocusRequestCompat {
        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(this)
            .setWillPauseWhenDucked(false) // not pause but providing my implementation
//            .setAcceptsDelayedFocusGain(true) TODO
            .setAudioAttributes(
                AudioAttributesCompat.Builder()
                    .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                    .build()
            )
            .build()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        assertMainThread()

        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.get().setVolume(this.volume.normal())
                if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED) {
                    player.get().resume()
                }
                currentFocus = FocusState.GAIN
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                currentFocus = FocusState.NONE
                player.get().pause(false, releaseFocus = true)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                if (player.get().isPlaying()) {
                    currentFocus = FocusState.PLAY_WHEN_READY
                }
                player.get().pause(false, currentFocus != FocusState.PLAY_WHEN_READY)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player.get().setVolume(this.volume.ducked())
            }

        }
    }
}
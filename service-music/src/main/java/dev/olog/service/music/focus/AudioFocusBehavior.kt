package dev.olog.service.music.focus

import android.app.Service
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import dagger.Lazy
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.internal.MediaSessionEvent
import dev.olog.service.music.internal.MediaSessionEventDispatcher
import dev.olog.shared.android.extensions.systemService
import dev.olog.shared.android.utils.assertMainThread
import dev.olog.shared.lazyFast
import javax.inject.Inject

internal class AudioFocusBehavior @Inject constructor(
    service: Service,
    private val eventDispatcher: Lazy<MediaSessionEventDispatcher>,
    private val player: Lazy<IPlayer>,
    private val volume: IMaxAllowedPlayerVolume

) : AudioManager.OnAudioFocusChangeListener {

    private val audioManager = service.systemService<AudioManager>()

    private val focusRequest by lazyFast { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    fun requestFocus(): Boolean {
        assertMainThread()

        val focus = requestFocusInternal()
        currentFocus = when (focus) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> FocusState.GAIN
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> FocusState.DELAYED
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> FocusState.NONE
            else -> error("audio focus response not handle with code $focus")
        }

        return (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
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
        onAudioFocusChangeInternal(AudioFocusType.get(focusChange))
    }

    private fun onAudioFocusChangeInternal(focus: AudioFocusType) {
        when (focus) {
            AudioFocusType.GAIN -> dispatchGain()
            AudioFocusType.LOSS -> dispatchLoss()
            AudioFocusType.LOSS_TRANSIENT -> dispatchLossTransient()
            AudioFocusType.LOSS_TRANSIENT_CAN_DUCK -> dispatchLossTransientCanDuck()
            else -> {}
        }
    }

    private fun dispatchGain() {
        player.get().setVolume(volume.normal())
        if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED) {
            eventDispatcher.get().nextEvent(MediaSessionEvent.Resume)
        }
        currentFocus = FocusState.GAIN
    }

    private fun dispatchLoss() {
        currentFocus = FocusState.NONE
        val event = MediaSessionEvent.Pause(stopService = false, releaseFocus = true)
        eventDispatcher.get().nextEvent(event)
    }

    private fun dispatchLossTransient() {
        if (player.get().isPlaying()) {
            currentFocus = FocusState.PLAY_WHEN_READY
        }
        val event = MediaSessionEvent.Pause(stopService = false, releaseFocus = currentFocus != FocusState.PLAY_WHEN_READY)
        eventDispatcher.get().nextEvent(event)
    }

    private fun dispatchLossTransientCanDuck() {
        player.get().setVolume(volume.ducked())
    }

}

private enum class AudioFocusType {
    NONE,

    GAIN,
    GAIN_TRANSIENT,
    GAIN_TRANSIENT_EXCLUSIVE,
    GAIN_TRANSIENT_MAY_DUCK,

    LOSS,
    LOSS_TRANSIENT,
    LOSS_TRANSIENT_CAN_DUCK;

    companion object {
        fun get(focus: Int): AudioFocusType {
            return when (focus) {
                AudioManager.AUDIOFOCUS_NONE -> NONE
                AudioManager.AUDIOFOCUS_GAIN -> GAIN
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> GAIN_TRANSIENT
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> GAIN_TRANSIENT_EXCLUSIVE
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK -> GAIN_TRANSIENT_MAY_DUCK
                AudioManager.AUDIOFOCUS_LOSS -> LOSS
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> LOSS_TRANSIENT
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> LOSS_TRANSIENT_CAN_DUCK
                else -> error("focus=$focus")
            }
        }
    }

}
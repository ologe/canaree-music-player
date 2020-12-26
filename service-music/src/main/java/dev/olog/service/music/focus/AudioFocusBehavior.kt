package dev.olog.service.music.focus

import android.app.Service
import android.media.AudioManager
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import dagger.Lazy
import dagger.hilt.android.scopes.ServiceScoped
import dev.olog.service.music.event.queue.MediaSessionEvent
import dev.olog.service.music.event.queue.MediaSessionEventHandler
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.shared.android.extensions.systemService
import dev.olog.shared.android.utils.assertMainThread
import dev.olog.shared.exhaustive
import dev.olog.shared.lazyFast
import javax.inject.Inject

@ServiceScoped
internal class AudioFocusBehavior @Inject constructor(
    service: Service,
    private val eventDispatcher: Lazy<MediaSessionEventHandler>,
    private val player: Lazy<IPlayer>,

) : AudioManager.OnAudioFocusChangeListener {

    private val audioManager = service.systemService<AudioManager>()

    private val focusRequest by lazyFast { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    fun requestFocus(): Boolean {
        assertMainThread()

        val focus = AudioManagerCompat.requestAudioFocus(audioManager, focusRequest)
        currentFocus = FocusState.fromPlatform(focus)

        return currentFocus == FocusState.GAIN
    }

    fun abandonFocus() {
        assertMainThread()

        currentFocus = FocusState.NONE
        AudioManagerCompat.abandonAudioFocusRequest(audioManager, focusRequest)
    }

    private fun buildFocusRequest(): AudioFocusRequestCompat {
        return AudioFocusRequestCompat.Builder(AudioManagerCompat.AUDIOFOCUS_GAIN)
            .setOnAudioFocusChangeListener(this)
//          from docs: When you need to pause playback rather than duck the volume, call setWillPauseWhenDucked(true)
            .setWillPauseWhenDucked(false)
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
        val focus = AudioFocusType.fromPlatform(focusChange)

        when (focus) {
            AudioFocusType.NONE -> {}
            AudioFocusType.GAIN -> dispatchGain()
            AudioFocusType.GAIN_TRANSIENT -> {}
            AudioFocusType.GAIN_TRANSIENT_EXCLUSIVE -> {}
            AudioFocusType.GAIN_TRANSIENT_MAY_DUCK -> {}
            AudioFocusType.LOSS -> dispatchLoss()
            AudioFocusType.LOSS_TRANSIENT -> dispatchLossTransient()
            AudioFocusType.LOSS_TRANSIENT_CAN_DUCK -> dispatchLossTransientCanDuck()

        }.exhaustive
    }

    private fun dispatchGain() {
        player.get().setDucking(false)

        if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED) {
            eventDispatcher.get().nextEvent(MediaSessionEvent.PlayerAction.Resume)
        }
        currentFocus = FocusState.GAIN
    }

    private fun dispatchLoss() {
        currentFocus = FocusState.NONE
        val event = MediaSessionEvent.PlayerAction.Pause(
            stopService = false,
            releaseFocus = true
        )
        eventDispatcher.get().nextEvent(event)
    }

    private fun dispatchLossTransient() {
        if (player.get().isPlaying()) {
            currentFocus = FocusState.PLAY_WHEN_READY
        }
        val event = MediaSessionEvent.PlayerAction.Pause(
            stopService = false,
            releaseFocus = currentFocus != FocusState.PLAY_WHEN_READY
        )
        eventDispatcher.get().nextEvent(event)
    }

    private fun dispatchLossTransientCanDuck() {
        player.get().setDucking(true)
    }

}

private enum class AudioFocusType {
    NONE,

    GAIN, // can play indefinitely
    GAIN_TRANSIENT, // can play for a short amount of time (notification)
    GAIN_TRANSIENT_EXCLUSIVE, // can't play anything
    GAIN_TRANSIENT_MAY_DUCK, // lower volume

    LOSS, // can not play
    LOSS_TRANSIENT,
    LOSS_TRANSIENT_CAN_DUCK; // lower volume

    companion object {
        fun fromPlatform(focus: Int): AudioFocusType {
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
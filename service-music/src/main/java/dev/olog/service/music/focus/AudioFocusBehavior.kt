package dev.olog.service.music.focus

import android.app.Service
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.media.AudioAttributesCompat
import androidx.media.AudioFocusRequestCompat
import androidx.media.AudioManagerCompat
import dagger.Lazy
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.model.FocusState
import dev.olog.shared.lazyFast
import javax.inject.Inject

internal class AudioFocusBehavior @Inject constructor(
    service: Service,
    private val player: Lazy<IPlayer>, // keep it lazy to avoid circular dependency
    private val volume: IMaxAllowedPlayerVolume

) : AudioManager.OnAudioFocusChangeListener {

    companion object {
        @JvmStatic
        private val TAG = "SM:${AudioFocusBehavior::class.java.simpleName}"
    }

    private val audioManager = service.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val focusRequest by lazyFast { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    fun requestFocus(): Boolean {

        val focus = requestFocusInternal()
        currentFocus = when (focus) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> FocusState.GAIN
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> FocusState.DELAYED
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> FocusState.NONE
            else -> throw IllegalStateException("audio focus response not handle with code $focus")
        }

        return (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED).also {
            Log.v(TAG, "request focus, granted=$it")
        }
    }

    fun abandonFocus() {
        Log.v(TAG, "release focus")

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
        onAudioFocusChangeInternal(AudioFocusType.get(focusChange))
    }

    private fun onAudioFocusChangeInternal(focus: AudioFocusType){
        Log.v(TAG, "on focus=$focus")
        when (focus) {
            AudioFocusType.GAIN -> {
                player.get().setVolume(this.volume.normal())
                if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED) {
                    player.get().resume()
                }
                currentFocus = FocusState.GAIN
            }
            AudioFocusType.LOSS -> {
                currentFocus = FocusState.NONE
                player.get().pause(false, releaseFocus = true)
            }
            AudioFocusType.LOSS_TRANSIENT -> {
                if (player.get().isPlaying()) {
                    currentFocus = FocusState.PLAY_WHEN_READY
                }
                player.get().pause(false, currentFocus != FocusState.PLAY_WHEN_READY)
            }
            AudioFocusType.LOSS_TRANSIENT_CAN_DUCK -> {
                player.get().setVolume(this.volume.ducked())
            }
            else -> {
                Log.w(TAG, "not handled $focus")
            }
        }
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
        @JvmStatic
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
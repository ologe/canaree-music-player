package dev.olog.msc.music.service.focus

import android.annotation.TargetApi
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.Lazy
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.shared.isOreo
import javax.inject.Inject

private enum class FocusState {
    NONE, PLAY_WHEN_READY, DELAYED, GAIN;

}

class AudioFocusBehavior @Inject constructor(
        private val player: Lazy<Player>,
        private val volume: IPlayerVolume,
        private val audioManager: Lazy<AudioManager>

) : AudioManager.OnAudioFocusChangeListener {

    @get:RequiresApi(Build.VERSION_CODES.O)
    private val focusRequest by lazy { buildFocusRequest() }
    private var currentFocus = FocusState.NONE

    private val focusLock = java.lang.Object()

    fun requestFocus(): Boolean{
        val focus = if (isOreo()){
            requestFocusForOreo()
        } else {
            requestFocusPreOreo()
        }
        currentFocus = when (focus){
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> FocusState.GAIN
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> FocusState.DELAYED
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> FocusState.NONE
            else -> throw IllegalStateException("audio focus response not handle with code $focus")
        }
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun abandonFocus(){
        currentFocus = FocusState.NONE
        if (isOreo()){
            audioManager.get().abandonAudioFocusRequest(focusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.get().abandonAudioFocus(this)
        }
    }

    @Suppress("DEPRECATION")
    private fun requestFocusPreOreo() : Int{
        return audioManager.get().requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun requestFocusForOreo(): Int {
        return audioManager.get().requestAudioFocus(focusRequest)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildFocusRequest(): AudioFocusRequest {
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this)
                .setWillPauseWhenDucked(false) // not pause but providing my implementation
                .setAcceptsDelayedFocusGain(true)
                .setAudioAttributes(
                        AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                )
                .build()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        synchronized(focusLock) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    player.get().setVolume(this.volume.normal())
                    if (currentFocus == FocusState.PLAY_WHEN_READY || currentFocus == FocusState.DELAYED){
                        player.get().resume()
                    }
                    currentFocus = FocusState.GAIN
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    currentFocus = FocusState.NONE
                    player.get().pause(false, releaseFocus = true)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (player.get().isPlaying()){
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
}
package dev.olog.msc.music.service.focus

import android.annotation.TargetApi
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import dagger.Lazy
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.isOreo
import javax.inject.Inject

class AudioFocusBehavior @Inject constructor(
        private val player: Lazy<Player>,
        private val volume: IPlayerVolume,
        private val audioManager: Lazy<AudioManager>

) : AudioManager.OnAudioFocusChangeListener {

    private var lastFocusRequest: AudioFocusRequest? = null

    fun requestFocus(): Boolean{
        return if (isOreo()){
            requestFocusForOreo()
        } else {
            requestFocusPreOreo()
        }
    }

    fun abandonFocus(){
        if (isOreo() && lastFocusRequest != null){
            audioManager.get().abandonAudioFocusRequest(lastFocusRequest)
        } else {
            @Suppress("DEPRECATION")
            audioManager.get().abandonAudioFocus(this)
        }
    }

    @Suppress("DEPRECATION")
    private fun requestFocusPreOreo() : Boolean{
        val focus = audioManager.get().requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun requestFocusForOreo(): Boolean {
        lastFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this)
                .setWillPauseWhenDucked(true) // not pause but providing my implementation
                .setAcceptsDelayedFocusGain(true)
                .build()

        val focus = audioManager.get().requestAudioFocus(lastFocusRequest)
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                player.get().setVolume(this.volume.normal())
            }
            AudioManager.AUDIOFOCUS_LOSS -> player.get().pause(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> player.get().pause(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player.get().setVolume(this.volume.ducked())
            }
        }
    }
}
package dev.olog.music_service.utils

import android.annotation.TargetApi
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import dev.olog.shared_android.isOreo

object AudioFocusBehavior {

    private var lastFocusRequest: AudioFocusRequest? = null

    fun requestFocus(audioManager: AudioManager, listener: AudioManager.OnAudioFocusChangeListener): Boolean{
        return if (isOreo()){
            requestFocusForOreo(audioManager, listener)
        } else {
            requestFocusPreOreo(audioManager, listener)
        }
    }

    fun abandonFocus(audioManager: AudioManager, listener: AudioManager.OnAudioFocusChangeListener){
        if (isOreo()){
            audioManager.abandonAudioFocusRequest(lastFocusRequest)
        } else {
            audioManager.abandonAudioFocus(listener)
        }
    }

    private fun requestFocusPreOreo(audioManager: AudioManager, listener: AudioManager.OnAudioFocusChangeListener) : Boolean{
        val focus = audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }


    @TargetApi(Build.VERSION_CODES.O)
    private fun requestFocusForOreo(audioManager: AudioManager, listener: AudioManager.OnAudioFocusChangeListener): Boolean{
        lastFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(listener)
                .setWillPauseWhenDucked(true) // not pause but providing my implementation
                .setAcceptsDelayedFocusGain(true)
                .build()

        val focus = audioManager.requestAudioFocus(lastFocusRequest)
        return focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

}
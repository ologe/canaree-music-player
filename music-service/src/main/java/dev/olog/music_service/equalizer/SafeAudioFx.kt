package dev.olog.music_service.equalizer

import android.media.audiofx.AudioEffect
import dev.olog.shared_android.BuildConfig

abstract class SafeAudioFx {

    protected var isReleased = false

    protected fun <T : AudioEffect> release(audioEffect: T){
        safeEdit {
            audioEffect.release()
            isReleased = true
        }
    }

    protected fun safeEdit(func: () -> Unit){
        if (!isReleased){
            try {
                func()
            } catch (ex: Exception){
                if (BuildConfig.DEBUG){
                    ex.printStackTrace()
                }
            }
        }
    }

}
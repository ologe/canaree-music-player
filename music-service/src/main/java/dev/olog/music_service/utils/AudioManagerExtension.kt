package dev.olog.music_service.utils

import android.media.AudioManager
import android.view.KeyEvent

fun AudioManager.dispatchEvent(keycode: Int){
    dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keycode))
    dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keycode))
}
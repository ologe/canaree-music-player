package dev.olog.service.music.focus

import android.media.AudioManager

internal enum class FocusState {
    NONE,
    PLAY_WHEN_READY,
    DELAYED,
    GAIN;

    companion object {

        fun fromPlatform(focus: Int): FocusState {
            return when (focus) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> GAIN
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> DELAYED
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> NONE
                else -> error("audio focus response not handle with code $focus")
            }
        }

    }

}
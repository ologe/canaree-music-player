package dev.olog.feature.media.extensions

import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat

fun MediaControllerCompat.skipToNext() {
    transportControls?.skipToNext()
}

fun MediaControllerCompat.skipToPrevious() {
    transportControls?.skipToPrevious()
}

fun MediaControllerCompat.playPause() {
    val playbackState = playbackState
    playbackState?.let {
        when (it.state) {
            PlaybackStateCompat.STATE_PLAYING -> transportControls?.pause()
            PlaybackStateCompat.STATE_PAUSED -> transportControls?.play()
            else -> {
            }
        }
    }
}

fun MediaControllerCompat.seekTo(pos: Long) {
    transportControls?.seekTo(pos)
}

fun MediaControllerCompat.toggleShuffleMode(){
    // state in cycled internally
    transportControls.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_INVALID)
}

fun MediaControllerCompat.toggleRepeatMode(){
    // state in cycled internally
    transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_INVALID)
}
package dev.olog.music_service.interfaces

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.music_service.model.MediaEntity

interface PlayerLifecycle {

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPrepare(entity: MediaEntity) {}
        fun onPlay(entity: MediaEntity) {}
        fun onResume() {}
        fun onPause() {}
        fun onStateChanged(state: PlaybackStateCompat){}
    }

}
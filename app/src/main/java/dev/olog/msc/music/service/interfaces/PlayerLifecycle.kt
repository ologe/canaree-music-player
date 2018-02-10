package dev.olog.msc.music.service.interfaces

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.music.service.model.MediaEntity

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
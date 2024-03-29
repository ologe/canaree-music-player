package dev.olog.feature.media.impl.interfaces

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.feature.media.impl.model.MetadataEntity

interface IPlayerLifecycle {

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPrepare(metadata: MetadataEntity) {}
        fun onMetadataChanged(metadata: MetadataEntity) {}
        fun onStateChanged(state: PlaybackStateCompat) {}
        fun onSeek(where: Long) {}
    }

}
package dev.olog.feature.media.interfaces

import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.feature.media.api.model.MetadataEntity

internal interface IPlayerLifecycle {

    fun addListener(listener: Listener)
    fun removeListener(listener: Listener)

    interface Listener {
        fun onPrepare(metadata: MetadataEntity) {}
        fun onMetadataChanged(metadata: MetadataEntity) {}
        fun onStateChanged(state: PlaybackStateCompat) {}
        fun onSeek(where: Long) {}
    }

}
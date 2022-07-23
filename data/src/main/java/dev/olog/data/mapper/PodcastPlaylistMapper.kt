@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.mapper

import dev.olog.core.entity.track.Playlist
import dev.olog.data.db.playlist.PlaylistEntity
import dev.olog.data.db.playlist.PodcastPlaylistEntity

internal inline fun PodcastPlaylistEntity.toDomain(): Playlist {
    return Playlist(
        this.id,
        this.name,
        this.size,
        true
    )
}

internal inline fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        this.id,
        this.name,
        this.size,
        false
    )
}
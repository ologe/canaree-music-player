@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.lib.mapper

import dev.olog.domain.entity.track.Playlist
import dev.olog.lib.model.db.PlaylistEntity
import dev.olog.lib.model.db.PodcastPlaylistEntity

internal inline fun PodcastPlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = this.id,
        title = this.name,
        size = this.size,
        isPodcast = true
    )
}

internal inline fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = this.id,
        title = this.name,
        size = this.size,
        isPodcast = false
    )
}
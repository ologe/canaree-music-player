@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.data.mapper

import dev.olog.core.entity.track.Playlist
import dev.olog.data.db.playlist.PodcastPlaylistEntity

@Deprecated("")
internal inline fun PodcastPlaylistEntity.toDomain(): Playlist {
    return Playlist(
        this.id.toString(),
        this.name,
        this.size.toString(),
        0,
        true
    )
}
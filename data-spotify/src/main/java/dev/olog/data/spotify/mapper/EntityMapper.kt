package dev.olog.data.spotify.mapper

import dev.olog.data.spotify.entity.GeneratedPlaylistEntity
import dev.olog.domain.entity.track.GeneratedPlaylist

internal fun GeneratedPlaylistEntity.toPlaylist(): GeneratedPlaylist {
    return GeneratedPlaylist(
        id = this.playlistId,
        title = this.title,
        size = this.tracks.size
    )
}
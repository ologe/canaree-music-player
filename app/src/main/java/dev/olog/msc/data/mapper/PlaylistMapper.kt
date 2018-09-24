package dev.olog.msc.data.mapper

import dev.olog.msc.data.entity.PodcastPlaylist
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.PlaylistType

fun PodcastPlaylist.toPlaylist(): Playlist {
    return Playlist(
            this.id,
            this.name,
            this.size,
            "",  // TODO images
            PlaylistType.PODCAST
    )
}
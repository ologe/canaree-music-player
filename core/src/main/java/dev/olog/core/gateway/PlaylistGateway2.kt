package dev.olog.core.gateway

import dev.olog.core.entity.Playlist
import dev.olog.core.entity.Song

interface PlaylistGateway2 :
    BaseGateway<Playlist, Id>,
    ChildHasTracks2<Song, Id> {

    fun getAllAutoPlaylists() : List<Playlist>

}
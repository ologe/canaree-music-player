package dev.olog.core.gateway

import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song

interface PlaylistGateway2 :
        BaseGateway2<Playlist, Id>,
        ChildHasTracks2<Song, Id>,
        HasMostPlayed2 {

    fun getAllAutoPlaylists(): List<Playlist>

}
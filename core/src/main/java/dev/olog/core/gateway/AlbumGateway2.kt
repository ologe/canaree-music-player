package dev.olog.core.gateway

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface AlbumGateway2 :
        BaseGateway2<Album, Id>,
        ChildHasTracks2<Song, Id>,
        HasLastPlayed<Album>,
        HasRecentlyAdded<Album>,
        HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

}
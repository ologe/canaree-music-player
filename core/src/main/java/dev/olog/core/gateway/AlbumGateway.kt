package dev.olog.core.gateway

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
        BaseGateway<Album, Id>,
        ChildHasTracks<Song, Id>,
        HasLastPlayed<Album>,
        HasRecentlyAdded<Album>,
        HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

}
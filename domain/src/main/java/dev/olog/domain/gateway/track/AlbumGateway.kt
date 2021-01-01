package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Id>,
    ChildHasTracks<Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

}
package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Id>,
    ChildHasTracks<Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

}
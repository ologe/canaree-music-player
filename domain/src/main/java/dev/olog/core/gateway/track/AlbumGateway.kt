package dev.olog.core.gateway.track

import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Long>,
    ChildHasTracks<Long>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album, Long> {

    fun observeArtistsAlbums(artistId: Long): Flow<List<Album>>

}
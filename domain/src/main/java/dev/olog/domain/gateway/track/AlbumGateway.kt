package dev.olog.domain.gateway.track

import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Long>,
    ChildHasTracks<Long>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    HasSiblings<Album, Long> {

    fun observeArtistsAlbums(artistId: Long): Flow<List<Album>>

}
package dev.olog.domain.gateway.podcast

import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface PodcastAlbumGateway :
    BaseGateway<Album, Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    ChildHasTracks<Id>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>
}
package dev.olog.core.gateway

import dev.olog.core.entity.track.Album
import kotlinx.coroutines.flow.Flow

interface PodcastAlbumGateway :
    BaseGateway<Album, Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    ChildHasTracks<Id>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>
}
package dev.olog.core.gateway.podcast

import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface PodcastAlbumGateway :
    BaseGateway<Album, Id>,
    HasLastPlayed<Album>,
    HasRecentlyAdded<Album>,
    ChildHasTracks<Id>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>
}
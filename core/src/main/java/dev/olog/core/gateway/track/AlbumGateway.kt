package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.CollectionDetailSort
import dev.olog.core.entity.sort.CollectionSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Album
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface AlbumGateway :
    BaseGateway<Album, Id>,
    ChildHasTracks<Id>,
    HasRecentlyAdded<Album>,
    HasLastPlayed<Album>,
    HasSiblings<Album, Id> {

    fun observeArtistsAlbums(artistId: Id): Flow<List<Album>>

    fun getSort(): Sort<CollectionSort>
    fun setSort(sort: Sort<CollectionSort>)

    fun getDetailSort(): Sort<CollectionDetailSort>
    fun observeDetailSort(): Flow<Sort<CollectionDetailSort>>
    fun setDetailSort(sort: Sort<CollectionDetailSort>)

}
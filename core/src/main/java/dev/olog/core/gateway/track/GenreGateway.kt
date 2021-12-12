package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.GenreDetailSort
import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Genre
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface GenreGateway :
    BaseGateway<Genre, Id>,
    ChildHasTracks<Id>,
    HasMostPlayed,
    HasSiblings<Genre, Id>,
    HasRelatedArtists<Id>,
    HasRecentlyAddedSongs<Id> {

    fun getSort(): Sort<GenericSort>
    fun setSort(sort: Sort<GenericSort>)

    fun getDetailSort(): Sort<GenreDetailSort>
    fun observeDetailSort(): Flow<Sort<GenreDetailSort>>
    fun setDetailSort(sort: Sort<GenreDetailSort>)

}
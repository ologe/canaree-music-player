package dev.olog.core.gateway.podcast

import dev.olog.core.entity.sort.AuthorDetailSort
import dev.olog.core.entity.sort.AuthorSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface PodcastAuthorGateway :
    BaseGateway<Artist, Id>,
    ChildHasTracks<Id>,
    HasRecentlyAdded<Artist>,
    HasLastPlayed<Artist> {

    fun getSort(): Sort<AuthorSort>
    fun setSort(sort: Sort<AuthorSort>)

    fun getDetailSort(): Sort<AuthorDetailSort>
    fun observeDetailSort(): Flow<Sort<AuthorDetailSort>>
    fun setDetailSort(sort: Sort<AuthorDetailSort>)

}
package dev.olog.core.gateway.track

import dev.olog.core.entity.sort.FolderDetailSort
import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Folder
import dev.olog.core.gateway.base.*
import kotlinx.coroutines.flow.Flow

interface FolderGateway :
    BaseGateway<Folder, String>,
    ChildHasTracks<String>,
    HasMostPlayed,
    HasSiblings<Folder, String>,
    HasRelatedArtists<String>,
    HasRecentlyAddedSongs<String> {

    fun getAllBlacklistedIncluded(): List<Folder>

    fun getSort(): Sort<GenericSort>
    fun setSort(sort: Sort<GenericSort>)

    fun getDetailSort(): Sort<FolderDetailSort>
    fun observeDetailSort(): Flow<Sort<FolderDetailSort>>
    fun setDetailSort(sort: Sort<FolderDetailSort>)

}
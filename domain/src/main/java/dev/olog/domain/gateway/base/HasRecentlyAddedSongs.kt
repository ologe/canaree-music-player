package dev.olog.domain.gateway.base

import dev.olog.domain.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasRecentlyAddedSongs <Param> {
    fun observeRecentlyAdded(param: Param): Flow<List<Song>>
}
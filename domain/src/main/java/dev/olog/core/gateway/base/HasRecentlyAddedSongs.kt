package dev.olog.core.gateway.base

import dev.olog.core.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface HasRecentlyAddedSongs <Param> {
    fun observeRecentlyAdded(param: Param): Flow<List<Track>>
}
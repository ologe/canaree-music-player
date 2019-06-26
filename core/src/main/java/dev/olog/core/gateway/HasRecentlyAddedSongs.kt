package dev.olog.core.gateway

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasRecentlyAddedSongs <Param> {
    fun observeRecentlyAdded(path: Param): Flow<List<Song>>
}
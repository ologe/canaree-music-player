package dev.olog.core.gateway.base

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface HasRecentlyAddedSongs <Param> {
    fun observeRecentlyAddedSongs(path: Param): Flow<List<Song>>
}
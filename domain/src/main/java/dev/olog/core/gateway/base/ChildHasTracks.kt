package dev.olog.core.gateway.base

import dev.olog.core.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<Param> {

    suspend fun getTrackListByParam(param: Param): List<Song>
    fun observeTrackListByParam(param: Param): Flow<List<Song>>

}
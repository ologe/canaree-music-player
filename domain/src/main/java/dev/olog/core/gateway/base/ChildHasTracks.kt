package dev.olog.core.gateway.base

import dev.olog.core.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<Param> {

    suspend fun getTrackListByParam(param: Param): List<Track>
    fun observeTrackListByParam(param: Param): Flow<List<Track>>

}
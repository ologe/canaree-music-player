package dev.olog.domain.gateway.base

import dev.olog.domain.entity.track.Track
import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<Param> {

    suspend fun getTrackListByParam(param: Param): List<Track>
    fun observeTrackListByParam(param: Param): Flow<List<Track>>

}
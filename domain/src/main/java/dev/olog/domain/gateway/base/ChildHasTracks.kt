package dev.olog.domain.gateway.base

import dev.olog.domain.entity.track.Song
import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<Param> {

    fun getTrackListByParam(param: Param): List<Song>
    fun observeTrackListByParam(param: Param): Flow<List<Song>>

}
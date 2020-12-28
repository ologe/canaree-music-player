package dev.olog.core.gateway.base

import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<Param, Song> {

    suspend fun getTrackListByParam(param: Param): List<Song>
    fun observeTrackListByParam(param: Param): Flow<List<Song>>

}
package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

interface ChildHasTracks<T, Param> {

    fun getTrackListByParam(param: Param): List<T>
    fun observeTrackListByParam(param: Param): Flow<List<T>>

}
package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

interface ChildHasTracks2<T, Param> {

    fun getTrackListByParam(param: Param): List<T>
    fun observeTrackListByParam(param: Param): Flow<List<T>>

}
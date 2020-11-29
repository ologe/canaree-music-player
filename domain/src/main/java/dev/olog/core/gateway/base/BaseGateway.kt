package dev.olog.core.gateway.base

import kotlinx.coroutines.flow.Flow

interface BaseGateway<T, Param> {
    suspend fun getAll(): List<T>
    fun observeAll(): Flow<List<T>>

    suspend fun getByParam(param: Param): T?
    fun observeByParam(param: Param): Flow<T?>
}
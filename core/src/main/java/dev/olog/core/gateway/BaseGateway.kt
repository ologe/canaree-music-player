package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

typealias Id = Long
typealias Path = String

interface BaseGateway<T, Param> {
    fun getAll(): List<T>
    fun observeAll(): Flow<List<T>>

    fun getByParam(param: Param): T?
    fun observeByParam(param: Param): Flow<T?>
}
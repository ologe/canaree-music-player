package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

interface HasSiblings<T, Param> {
    fun observeSiblings(param: Param): Flow<List<T>>
}
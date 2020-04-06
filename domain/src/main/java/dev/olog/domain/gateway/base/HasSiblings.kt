package dev.olog.domain.gateway.base

import kotlinx.coroutines.flow.Flow

interface HasSiblings<T, Param> {
    fun observeSiblings(param: Param): Flow<List<T>>
}
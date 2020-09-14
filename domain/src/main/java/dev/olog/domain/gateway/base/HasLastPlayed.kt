package dev.olog.domain.gateway.base

import kotlinx.coroutines.flow.Flow

interface HasLastPlayed<T> {
    fun observeLastPlayed(): Flow<List<T>>
    suspend fun addLastPlayed(id: Long)
}
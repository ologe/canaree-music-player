package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

interface HasLastPlayed<T> {
    fun observeLastPlayed(): Flow<List<T>>
    suspend fun addLastPlayed(id: Id)
}
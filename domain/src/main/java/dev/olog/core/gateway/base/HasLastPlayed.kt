package dev.olog.core.gateway.base

import kotlinx.coroutines.flow.Flow

interface HasLastPlayed<T> {
    companion object {
        const val MIN_ITEMS = 5
        const val MAX_ITEM_TO_SHOW = 10
    }


    fun observeLastPlayed(): Flow<List<T>>
    suspend fun addLastPlayed(id: Long)
}
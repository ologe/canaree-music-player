package dev.olog.core.gateway.base

import kotlinx.coroutines.flow.Flow

interface HasLastPlayed<T> {
    companion object {
        @Deprecated("delete")
        const val MIN_ITEMS = 5
        @Deprecated("replace with DataConstants")
        const val MAX_ITEM_TO_SHOW = 10
    }


    fun observeLastPlayed(): Flow<List<T>>
    suspend fun addLastPlayed(id: Id)
}
package dev.olog.core.gateway

import kotlinx.coroutines.flow.Flow

interface HasRecentlyAdded <T> {
    fun observeRecentlyAdded(): Flow<List<T>>
}
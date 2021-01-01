package dev.olog.domain.gateway.base

import kotlinx.coroutines.flow.Flow

interface HasRecentlyAdded <T> {
    fun observeRecentlyAdded(): Flow<List<T>>
}
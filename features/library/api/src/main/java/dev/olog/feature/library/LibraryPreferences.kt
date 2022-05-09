package dev.olog.feature.library

import kotlinx.coroutines.flow.Flow

interface LibraryPreferences {

    fun getSpanCount(category: TabCategory): Int
    fun observeSpanCount(category: TabCategory): Flow<Int>
    fun setSpanCount(category: TabCategory, spanCount: Int)

}
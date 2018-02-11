package dev.olog.msc.data.repository

import java.util.*

class CachedDataStore<T> {

    private val cache = Vector<T>()

    @Synchronized
    fun updateCache(list: List<T>) {
        if (this.cache != list){
            this.cache.clear()
            this.cache.addAll(list)
        }
    }

    fun getAll(): List<T> = cache.toList()

}
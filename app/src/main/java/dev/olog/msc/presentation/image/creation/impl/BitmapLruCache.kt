package dev.olog.msc.presentation.image.creation.impl

import android.graphics.Bitmap
import android.util.LruCache

object BitmapLruCache {

    private const val MiB = 1024 * 1024
    private const val CACHE_SIZE = 10 * MiB

    private val cache = LruCache<Long, Bitmap>(CACHE_SIZE)

    fun put(albumId: Long, bitmap: Bitmap){
        cache.put(albumId, bitmap)
    }

    fun get(albumId: Long): Bitmap? {
        return cache.get(albumId)
    }

}
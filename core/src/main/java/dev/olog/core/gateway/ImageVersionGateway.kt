package dev.olog.core.gateway

import dev.olog.core.MediaId
import java.util.concurrent.ConcurrentHashMap

// hacky workaround
object CachedImageVersion {
    val map: MutableMap<MediaId, Int> = ConcurrentHashMap()
}

interface ImageVersionGateway {

    fun getCurrentVersion(mediaId: MediaId): Int
    fun setCurrentVersion(mediaId: MediaId, version: Int)
    fun increaseCurrentVersion(mediaId: MediaId)

    fun deleteAll()

}
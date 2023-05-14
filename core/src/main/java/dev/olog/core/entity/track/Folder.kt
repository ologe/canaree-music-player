package dev.olog.core.entity.track

import dev.olog.core.MediaId

// TODO ensure only songs, or add support to podcast as well
data class Folder(
    val id: Long,
    val title: String,
    val path: String,
    val size: Int
) {


    fun getMediaId(): MediaId {
        return MediaId.ofFolder(id, false)
    }

}
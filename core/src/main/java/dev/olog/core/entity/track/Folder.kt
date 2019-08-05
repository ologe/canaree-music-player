package dev.olog.core.entity.track

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory

class Folder(
    @JvmField
    val title: String,
    @JvmField
    val path: String,
    @JvmField
    val size: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Folder

        if (title != other.title) return false
        if (path != other.path) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + size
        return result
    }

    fun getMediaId(): MediaId {
        return MediaId.createCategoryValue(MediaIdCategory.FOLDERS, this.path)
    }

}
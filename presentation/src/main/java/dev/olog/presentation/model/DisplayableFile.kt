package dev.olog.presentation.model

import dev.olog.core.MediaId
import java.io.File

class DisplayableFile(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId,
    @JvmField
    val title: String,
    @JvmField
    val path: String?
) : BaseModel {

    fun isFile(): Boolean = path != null
    fun asFile(): File = File(path!!)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayableFile

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (title != other.title) return false
        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (path?.hashCode() ?: 0)
        return result
    }


}
package dev.olog.core.entity

import android.provider.MediaStore

class LastMetadata(
    @JvmField
    val title: String,
    @JvmField
    val subtitle: String,
    @JvmField
    val id: Long
) {

    fun isNotEmpty(): Boolean {
        return title.isNotBlank()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastMetadata

        if (title != other.title) return false
        if (subtitle != other.subtitle) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + subtitle.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    val description: String
        get() {
            if (subtitle == MediaStore.UNKNOWN_STRING) {
                return title
            }
            return "$title $subtitle"
        }



}
package dev.olog.presentation.model

import dev.olog.core.MediaId

class LicenseModel(
    @JvmField
    override val type: Int,
    @JvmField
    override val mediaId: MediaId,
    @JvmField
    val name: String,
    @JvmField
    val url: String,
    @JvmField
    val license: String
) : BaseModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LicenseModel

        if (type != other.type) return false
        if (mediaId != other.mediaId) return false
        if (name != other.name) return false
        if (url != other.url) return false
        if (license != other.license) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + license.hashCode()
        return result
    }
}
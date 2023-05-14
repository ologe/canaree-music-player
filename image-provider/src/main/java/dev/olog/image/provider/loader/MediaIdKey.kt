package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import java.security.MessageDigest

internal class MediaIdKey(
    private val mediaId: MediaId
) : Key {

    override fun toString(): String {
        return "${mediaId.category.key}-${mediaId.id}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaIdKey

        return this.mediaId.category == other.mediaId.category &&
                this.mediaId.id == other.mediaId.id

    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + mediaId.category.key.hashCode()
        result = 31 * result + mediaId.id.hashCode()
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}

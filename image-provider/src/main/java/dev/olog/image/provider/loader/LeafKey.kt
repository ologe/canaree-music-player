package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import java.security.MessageDigest

// TODO check also against date modified?
// checks only on id. it's the same between different categories
internal class LeafKey(
    mediaId: MediaId
) : Key {

    private val id = mediaId.leaf!!

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LeafKey

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "LeafKey(id=$id)"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }
}
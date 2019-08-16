package dev.olog.image.provider

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import java.nio.ByteBuffer
import java.security.MessageDigest

class CustomMediaStoreSignature(
    @JvmField
    private val mediaId: MediaId,
    @JvmField
    private val version: Int
) : Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(Integer.SIZE)
            .putInt(version)
            .array()
        messageDigest.update(data)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomMediaStoreSignature

        if (mediaId != other.mediaId) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + version
        return result
    }


}
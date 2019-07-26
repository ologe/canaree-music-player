package dev.olog.image.provider

import com.bumptech.glide.load.Key
import java.nio.ByteBuffer
import java.security.MessageDigest

data class CustomMediaStoreSignature(
    private val hasGlideSignature: HasGlideSignature
) : Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(Integer.SIZE).putInt(hasGlideSignature.getCurrentVersion()).array()
        messageDigest.update(data)
    }

}
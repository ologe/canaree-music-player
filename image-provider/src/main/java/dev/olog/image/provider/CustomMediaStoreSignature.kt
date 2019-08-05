package dev.olog.image.provider

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageVersionGateway
import java.nio.ByteBuffer
import java.security.MessageDigest

class CustomMediaStoreSignature(
    @JvmField
    private val mediaId: MediaId,
    @JvmField
    private val versionGateway: ImageVersionGateway
) : Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val data = ByteBuffer.allocate(Integer.SIZE)
            .putInt(versionGateway.getCurrentVersion(mediaId))
            .array()
        messageDigest.update(data)
    }

}
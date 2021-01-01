package dev.olog.lib.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import java.security.MessageDigest

internal data class MediaIdKey(
    private val mediaId: MediaId
) : Key {

    override fun toString(): String {
        if (mediaId is MediaId.Track) {
            return "${MediaIdCategory.SONGS}-${mediaId.id}"
        }
        return "${mediaId.category.name}-${mediaId.categoryValue}"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}

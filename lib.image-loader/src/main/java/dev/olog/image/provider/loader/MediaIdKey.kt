package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.SONGS
import java.security.MessageDigest

internal class MediaIdKey(
    private val mediaId: MediaId
) : Key {

    override fun toString(): String {
        return when (mediaId) {
            is MediaId.Track -> "$SONGS-${mediaId.id}"
            is MediaId.Category -> "${mediaId.category.name}-${mediaId.categoryId}"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaIdKey

        if (this.mediaId is MediaId.Track && other.mediaId is MediaId.Track) {
            // is song
            return this.mediaId.id == other.mediaId.id
        }
        return this.mediaId.category == other.mediaId.category &&
                this.mediaId.categoryId == other.mediaId.categoryId
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + mediaId.category.name.hashCode()
        result = 31 * result + mediaId.categoryId.hashCode()
        if (mediaId is MediaId.Track) {
            result = 31 * result + mediaId.id.hashCode()
        }
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}

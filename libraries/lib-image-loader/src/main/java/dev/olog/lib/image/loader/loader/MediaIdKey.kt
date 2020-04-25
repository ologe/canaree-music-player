package dev.olog.lib.image.loader.loader

import com.bumptech.glide.load.Key
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.SONGS
import java.security.MessageDigest

internal object MediaIdKey{

    fun create(mediaId: MediaId): Key {
        return when (mediaId) {
            is MediaId.Track -> TrackKey(mediaId)
            is MediaId.Category -> CategoryKey(mediaId)
        }
    }

    private class CategoryKey(
        private val mediaId: MediaId.Category
    ): Key {

        override fun toString(): String {
            return "${mediaId.category}-${mediaId.categoryId}"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CategoryKey

            return mediaId.categoryId == other.mediaId.categoryId
        }

        override fun hashCode(): Int {
            var result = 17
            result = 31 * result + mediaId.category.name.hashCode()
            result = 31 * result + mediaId.categoryId.hashCode()
            return result
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(this.toString().toByteArray(Key.CHARSET))
        }
    }

    private class TrackKey(
        private val mediaId: MediaId.Track
    ): Key {

        override fun toString(): String {
            if (mediaId.isSpotify) {
                return mediaId.categoryId
            }
            return mediaId.id
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TrackKey

            if (mediaId.isSpotify) {
                return mediaId.categoryId == other.mediaId.categoryId
            }

            return mediaId.id == other.mediaId.id
        }

        override fun hashCode(): Int {
            if (mediaId.isSpotify) {
                return mediaId.categoryId.hashCode()
            }
            return mediaId.id.hashCode()
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(this.toString().toByteArray(Key.CHARSET))
        }
    }

}

package dev.olog.image.provider.loader

import com.bumptech.glide.load.Key
import dev.olog.core.MediaId
import java.security.MessageDigest

// check on category and id
internal class CategoryKey(
    mediaId: MediaId
) : Key {

    private val category = mediaId.category
    private val id = mediaId.categoryValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryKey

        if (category != other.category) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return "CategoryKey(category=$category, id='$id')"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }


}
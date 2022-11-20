package dev.olog.image.provider.loader

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.Key
import java.security.MessageDigest

class DrawableKey(
    private val drawable: Drawable,
    private val width: Int,
    private val height: Int,
) : Key {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrawableKey

        if (drawable != other.drawable) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = drawable.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    override fun toString(): String {
        return "DrawableKey(drawable=$drawable, width=$width, height=$height)"
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }
}
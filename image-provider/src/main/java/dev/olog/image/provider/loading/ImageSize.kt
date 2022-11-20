package dev.olog.image.provider.loading

import com.bumptech.glide.request.target.Target

sealed class ImageSize {

    abstract val size: Int

    object Small : ImageSize() { override val size: Int = 150 }
    object Medium : ImageSize() { override val size: Int = 400 }
    object Large : ImageSize() { override val size: Int = 1000 }
    object Original : ImageSize() { override val size: Int = Target.SIZE_ORIGINAL }
    data class Custom(override val size: Int) : ImageSize()
}
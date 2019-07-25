package dev.olog.shared.theme

import kotlinx.coroutines.channels.ReceiveChannel

interface HasImageShape {
    fun getImageShape(): ImageShape
    fun observeImageShape(): ReceiveChannel<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND
}


package dev.olog.platform.theme

import kotlinx.coroutines.channels.ReceiveChannel

interface HasImageShape {
    fun getImageShape(): ImageShape
    fun observeImageShape(): ReceiveChannel<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}


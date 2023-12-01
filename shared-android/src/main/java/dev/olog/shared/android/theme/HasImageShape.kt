package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.StateFlow

interface HasImageShape {
    fun observeImageShape(): StateFlow<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}


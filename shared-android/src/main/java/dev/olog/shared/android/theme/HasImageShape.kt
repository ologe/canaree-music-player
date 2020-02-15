package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.Flow

interface HasImageShape {
    fun getImageShape(): ImageShape
    fun observeImageShape(): Flow<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}


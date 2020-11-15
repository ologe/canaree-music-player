package dev.olog.shared.android.theme

import kotlinx.coroutines.flow.Flow

interface ImageShapeAmbient {
    val value: ImageShape
    val flow: Flow<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}


package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext
import kotlinx.coroutines.channels.ReceiveChannel

fun Context.hasImageShape(): HasImageShape {
    return applicationContext.findInContext()
}

interface HasImageShape {
    fun getImageShape(): ImageShape
    fun observeImageShape(): ReceiveChannel<ImageShape>
}

enum class ImageShape {
    RECTANGLE, ROUND, CUT_CORNER
}


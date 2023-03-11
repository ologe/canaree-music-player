package dev.olog.platform.theme

import android.content.Context
import dev.olog.platform.extension.findInContext
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


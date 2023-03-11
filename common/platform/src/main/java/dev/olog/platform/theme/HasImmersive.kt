package dev.olog.platform.theme

import android.content.Context
import dev.olog.platform.extension.findInContext

fun Context.hasImmersive(): HasImmersive {
    return applicationContext.findInContext()
}

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = hasImmersive().isImmersive()
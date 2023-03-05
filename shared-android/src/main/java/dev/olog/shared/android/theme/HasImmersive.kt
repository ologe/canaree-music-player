package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext

fun Context.hasImmersive(): HasImmersive {
    return applicationContext.findInContext()
}

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = hasImmersive().isImmersive()
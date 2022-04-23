package dev.olog.platform.theme

import android.content.Context
import dev.olog.shared.extension.findInContext

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = (this.applicationContext.findInContext<HasImmersive>()).isImmersive()
package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.findInContext

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = (this.applicationContext.findInContext<HasImmersive>()).isImmersive()
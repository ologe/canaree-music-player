package dev.olog.shared.android.theme

import android.content.Context
import dev.olog.shared.android.extensions.asType

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = (this.applicationContext.asType<HasImmersive>()).isImmersive()
package dev.olog.shared.android.theme

import android.content.Context

interface HasImmersive {
    fun isImmersive(): Boolean
}

fun Context.isImmersiveMode(): Boolean = (this.applicationContext as HasImmersive).isImmersive()
package dev.olog.image.provider

import android.content.Context

interface HasGlideSignature {

    fun getCurrentVersion(): Int
    fun increaseCurrentVersion()

}

fun Context.hasGlideSignature(): HasGlideSignature = applicationContext as HasGlideSignature
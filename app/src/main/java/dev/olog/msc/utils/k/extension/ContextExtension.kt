package dev.olog.msc.utils.k.extension

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.configuration

val Context.isPortrait: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.isLandscape: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawable {
    return this.getDrawable(id) as AnimatedVectorDrawable
}

fun Context.hasPermission(permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
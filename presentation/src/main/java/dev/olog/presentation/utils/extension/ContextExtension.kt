package dev.olog.presentation.utils.extension

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.DrawableRes
import org.jetbrains.anko.configuration

val Context.isPortrait: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.isLandscape: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawable {
    return this.getDrawable(id) as AnimatedVectorDrawable
}
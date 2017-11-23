package dev.olog.presentation.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.DrawableRes
import org.jetbrains.anko.configuration

val Context.isPortrait: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT


fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawable {
    return this.getDrawable(id) as AnimatedVectorDrawable
}
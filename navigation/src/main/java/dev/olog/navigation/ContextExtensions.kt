package dev.olog.navigation

import android.content.Context
import android.graphics.Color
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.core.content.res.use

internal fun Context.themeAttributeToColor(@AttrRes themeAttributeId: Int): Int {
    return obtainStyledAttributes(intArrayOf(themeAttributeId)).use {
        it.getColor(0, Color.MAGENTA)
    }
}

internal fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
    val id = obtainStyledAttributes(intArrayOf(attr)).use {
        it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
    }
    return AnimationUtils.loadInterpolator(this, id)
}
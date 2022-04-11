/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.olog.service.floating.api

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.*
import android.widget.RelativeLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import dev.olog.service.floating.R
import dev.olog.shared.android.extensions.dimen
import dev.olog.shared.lazyFast
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Fullscreen View that provides an exit "drop zone" for users to exit the Hover Menu.
 */
internal class ExitView constructor(
    context: Context
) : RelativeLayout(context) {

    companion object {
        private const val TAG = "ExitView"
    }

    private var mExitRadiusInPx: Int = 0
    private var mExitIcon: View? = null

    private val exitZoneCenter: Point by lazyFast {
        Point(
            (mExitIcon!!.x + mExitIcon!!.width / 2).toInt(),
            (mExitIcon!!.y + mExitIcon!!.height / 2).toInt()
        )
    }

    private var currentAnimation: AnimatorSet? = null

    private val gradient by lazyFast { findViewById<View>(R.id.view_exit_gradient) }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_hover_menu_exit, this, true)

        mExitIcon = findViewById(R.id.view_exit)

        mExitRadiusInPx = context.dimen(R.dimen.hover_exit_radius)

    }

    fun isInExitZone(position: Point): Boolean {
        val exitCenter = exitZoneCenter
        val distanceToExit = calculateDistance(position, exitCenter)
        Log.d(TAG, "Drop point: $position, Exit center: $exitCenter, Distance: $distanceToExit")
        return distanceToExit <= mExitRadiusInPx
    }

    fun show() {
        scaleY = 1f
        scaleX = 1f
        gradient.scaleX = .4f
        gradient.scaleY = .8f
        alpha = 0f

        currentAnimation?.cancel()
        currentAnimation = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            doOnStart { isVisible = true }
            playTogether(
                ObjectAnimator.ofFloat(this@ExitView, "alpha", 1f),
                ObjectAnimator.ofFloat(gradient, "scaleX", 1f),
                ObjectAnimator.ofFloat(gradient, "scaleY", 1f)
            )
        }
        currentAnimation!!.start()
    }

    fun hide() {
        pivotY = context.resources.displayMetrics.heightPixels.toFloat() * 1.2f

        currentAnimation?.cancel()
        currentAnimation = AnimatorSet().apply {
            interpolator = AccelerateInterpolator()
            duration = 250L
            playTogether(
                ObjectAnimator.ofFloat(this@ExitView, "alpha", 0f),
                ObjectAnimator.ofFloat(this@ExitView, "scaleX", 0f),
                ObjectAnimator.ofFloat(this@ExitView, "scaleY", 0f)
            )
            doOnEnd { isVisible = false }
        }
        currentAnimation!!.start()
    }

    private fun calculateDistance(p1: Point, p2: Point): Double {
        return sqrt((p2.x - p1.x).toDouble().pow(2.0) + (p2.y - p1.y).toDouble().pow(2.0))
    }
}

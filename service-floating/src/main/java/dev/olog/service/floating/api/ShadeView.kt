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

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import dev.olog.service.floating.R
import dev.olog.platform.extension.setGone
import dev.olog.platform.extension.setVisible


/**
 * Fullscreen `View` that appears behind the other visual elements in a [HoverView] and
 * darkens the background.
 */
internal class ShadeView constructor(
    context: Context
) : FrameLayout(context) {

    companion object {
        private const val FADE_DURATION = 200L
    }

    private var currentAnimation: ObjectAnimator? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_shade, this, true)
        alpha = 0f
        visibility = View.GONE
    }

    // fade int
    fun show() {
        currentAnimation?.cancel()
        currentAnimation = ObjectAnimator.ofFloat(this, "alpha", 1.0f).apply {
            duration = FADE_DURATION
            doOnStart { setVisible() }
        }
        currentAnimation!!.start()
    }

    // fade out
    fun hide() {
        currentAnimation?.cancel()

        currentAnimation = ObjectAnimator.ofFloat(this, "alpha", 0.0f).apply {
            duration = FADE_DURATION
            doOnEnd { setGone() }
        }
        currentAnimation!!.start()
    }

    fun hideImmediate() {
        visibility = View.GONE
    }
}

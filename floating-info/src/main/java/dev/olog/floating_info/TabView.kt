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
package dev.olog.floating_info

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View

/**
 * Visual representation of a top-level tab in a Hover menu.
 */
class TabView (
        context: Context,
        @DrawableRes backgroundDrawable: Int,
        @DrawableRes iconDrawable: Int

) : View(context) {

    private val mCircleDrawable: Drawable? = ContextCompat.getDrawable(context, backgroundDrawable)
    private var mIconDrawable: Drawable? = ContextCompat.getDrawable(context, iconDrawable)
    private var mIconInsetLeft: Int = 0
    private var mIconInsetTop: Int = 0
    private var mIconInsetRight: Int = 0
    private var mIconInsetBottom: Int = 0

    init {
        val insetsDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics).toInt()
        mIconInsetBottom = insetsDp
        mIconInsetRight = mIconInsetBottom
        mIconInsetTop = mIconInsetRight
        mIconInsetLeft = mIconInsetTop
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Make circle as large as View minus padding.
        mCircleDrawable!!.setBounds(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)

        // Re-size the icon as necessary.
        updateIconBounds()

        invalidate()
    }

    private fun updateIconBounds() {
        if (mIconDrawable != null) {
            val bounds = Rect(mCircleDrawable!!.bounds)
            bounds.set(bounds.left + mIconInsetLeft, bounds.top + mIconInsetTop, bounds.right - mIconInsetRight, bounds.bottom - mIconInsetBottom)
            mIconDrawable?.bounds = bounds
        }
    }

    override fun onDraw(canvas: Canvas) {
        mCircleDrawable!!.draw(canvas)
        mIconDrawable?.draw(canvas)
    }

    fun setIcon(@DrawableRes iconDrawable: Int){
        mIconDrawable = ContextCompat.getDrawable(context, iconDrawable)
        invalidate()
    }

}

package dev.olog.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class StoppingViewPager (
        context: Context,
        attrs: AttributeSet

) : ViewPager(context, attrs) {

    var isSwipeEnabled = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnabled){
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnabled){
            return super.onTouchEvent(ev)
        }
        return false
    }

}
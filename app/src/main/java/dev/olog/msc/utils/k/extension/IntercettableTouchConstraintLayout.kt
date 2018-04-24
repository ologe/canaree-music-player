package dev.olog.msc.utils.k.extension

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.MotionEvent

class IntercettableTouchConstraintLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

): ConstraintLayout(context, attrs) {

    private var touchEnabled = true

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !touchEnabled
    }

    fun setTouchEnabled(enable: Boolean){
        this.touchEnabled = enable
    }

}
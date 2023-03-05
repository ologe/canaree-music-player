package dev.olog.presentation.widgets.swipeableview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.R
import dev.olog.presentation.interfaces.slidingPanel
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.Flow

class SwipeableView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private val helper by lazyFast {
        SwipeableViewHelper(
            this,
            skipAreaDimension
        )
    }
    private val debugHelper by lazyFast {
        SwipeableViewDebug(
            this,
            skipAreaDimension
        )
    }

    private var isTouchEnabled = true

    private var skipAreaDimension : Int = 0
    private var debug: Boolean = false

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeableView)

        skipAreaDimension = a.getDimension(R.styleable.SwipeableView_skip_area_dimension, context.dipf(64)).toInt()

        debug = a.getBoolean(R.styleable.SwipeableView_debug, false)

        a.recycle()
    }

    fun setOnSwipeListener(swipeListener: SwipeListener?) {
        helper.swipeListener = swipeListener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode) {
            context.slidingPanel.addPanelSlideListener(slidingPanelListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        helper.swipeListener = null
        context.slidingPanel.removePanelSlideListener(slidingPanelListener)
    }

    fun isTouching(): Flow<Boolean> = helper.isTouching()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isTouchEnabled){
            return false
        }


        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> helper.onTouchDown(event)
            MotionEvent.ACTION_MOVE -> helper.onTouchMove(event)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> helper.onTouchUp(event)
            else -> super.onTouchEvent(event)
        }
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isTouchEnabled = newState == BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (isInEditMode && debug){
            debugHelper.draw(canvas)
        }
    }

    interface SwipeListener {
        fun onSwipedLeft() {}
        fun onSwipedRight() {}
        fun onClick() {}
        fun onLeftEdgeClick() {}
        fun onRightEdgeClick() {}
    }
}

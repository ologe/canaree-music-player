package dev.olog.presentation.widgets

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.MotionEvent

class SwipeableImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_SWIPED_THRESHOLD = 100
    }

    private val swipedThreshold: Int = DEFAULT_SWIPED_THRESHOLD
    private var xDown: Float = 0.toFloat()
    private var xUp: Float = 0.toFloat()
    private var yDown: Float = 0.toFloat()
    private var yUp: Float = 0.toFloat()
    private var swipeListener: SwipeListener? = null

    fun setOnSwipeListener(swipeListener: SwipeListener) {
        this.swipeListener = swipeListener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onActionDown(event)
            MotionEvent.ACTION_UP  -> onActionUp(event)
            else -> { }
        }
        return super.onTouchEvent(event)
    }

    private fun onActionDown(event: MotionEvent) {
        xDown = event.x
        yDown = event.y
    }

    private fun onActionUp(event: MotionEvent) {
        xUp = event.x
        yUp = event.y
        val swipedHorizontally = Math.abs(xUp - xDown) > swipedThreshold
        val swipedVertically = Math.abs(yUp - yDown) > swipedThreshold

        val isHorizontalScroll = swipedHorizontally && Math.abs(xUp - xDown) > Math.abs(yUp - yDown)

        if (isHorizontalScroll) {
            val swipedRight = xUp > xDown
            val swipedLeft = xUp < xDown

            if (swipedRight) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipedRight()
                }
            }
            if (swipedLeft) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipedLeft()
                }
            }
        }

        if (!swipedHorizontally && !swipedVertically) {
            if (swipeListener != null) {
                swipeListener!!.onClick()
            }
        }
    }

    interface SwipeListener {

        fun onSwipedLeft() {}
        fun onSwipedRight() {}
        fun onClick() {}
    }
}

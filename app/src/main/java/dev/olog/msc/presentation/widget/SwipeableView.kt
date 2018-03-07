package dev.olog.msc.presentation.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dev.olog.msc.utils.k.extension.dip
import io.reactivex.subjects.PublishSubject

private const val DEFAULT_SWIPED_THRESHOLD = 100

class SwipeableView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : View(context, attrs, defStyleAttr) {

    private val swipedThreshold = DEFAULT_SWIPED_THRESHOLD
    private var xDown = 0f
    private var xUp = 0f
    private var yDown = 0f
    private var yUp = 0f
    private var swipeListener: SwipeListener? = null
    private val isTouchingPublisher = PublishSubject.create<Boolean>()

    private val sixtyFourDip by lazy(LazyThreadSafetyMode.NONE) { context.dip(64) }

    fun setOnSwipeListener(swipeListener: SwipeListener?) {
        this.swipeListener = swipeListener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.swipeListener = null
    }

    fun isTouching() = isTouchingPublisher.distinctUntilChanged()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchingPublisher.onNext(true)
                onActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                isTouchingPublisher.onNext(true)
                return true
            }
            MotionEvent.ACTION_UP  -> {
                isTouchingPublisher.onNext(false)
                onActionUp(event)
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun onActionDown(event: MotionEvent) : Boolean{
        xDown = event.x
        yDown = event.y
        return true
    }

    private fun onActionUp(event: MotionEvent) : Boolean {
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
                    return true
                }
            }
            if (swipedLeft) {
                if (swipeListener != null) {
                    swipeListener!!.onSwipedLeft()
                    return true
                }
            }
        }

        if (!swipedHorizontally && !swipedVertically) {
            when {
                xDown < sixtyFourDip -> swipeListener?.onLeftEdgeClick()
                (width - xDown) < sixtyFourDip -> swipeListener?.onRightEdgeClick()
                else -> swipeListener?.onClick()
            }
            return true
        }
        return false
    }

    interface SwipeListener {
        fun onSwipedLeft() {}
        fun onSwipedRight() {}
        fun onClick() {}
        fun onLeftEdgeClick(){}
        fun onRightEdgeClick(){}
    }
}

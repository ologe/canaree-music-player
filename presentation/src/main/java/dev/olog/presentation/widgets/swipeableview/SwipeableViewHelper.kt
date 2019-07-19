package dev.olog.presentation.widgets.swipeableview

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import dev.olog.shared.extensions.findChild
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.widgets.ForegroundImageView
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import kotlin.math.abs

internal class SwipeableViewHelper(
    private val view: View,
    private val skipAreaDimension: Int
) {

    companion object {
        private const val DEFAULT_SWIPED_THRESHOLD = 50
    }

    private var xDown = 0f
    private var xUp = 0f
    private var yDown = 0f
    private var yUp = 0f

    private val swipedThreshold = DEFAULT_SWIPED_THRESHOLD

    var swipeListener: SwipeableView.SwipeListener? = null

    private val cover by lazyFast { findCover() }

    private val isTouchingPublisher = PublishProcessor.create<Boolean>()

    private fun findCover(): ForegroundImageView? {
        if (view.parent is ViewGroup) {
            return (view.parent as ViewGroup).findChild { it is ForegroundImageView } as ForegroundImageView?
        }
        return null
    }

    fun onTouchDown(event: MotionEvent): Boolean {
        view.parent.requestDisallowInterceptTouchEvent(true)
        isTouchingPublisher.onNext(true)
        return onActionDown(event)
    }

    fun onTouchMove(event: MotionEvent): Boolean{
        onActionMove(event)
        isTouchingPublisher.onNext(true)
        return true
    }

    fun onTouchUp(event: MotionEvent): Boolean{
        view.parent.requestDisallowInterceptTouchEvent(false)
        isTouchingPublisher.onNext(false)
        return onActionUp(event)
    }

    private fun onActionDown(event: MotionEvent): Boolean {
        xDown = event.x
        yDown = event.y
        return true
    }

    private fun onActionMove(event: MotionEvent) {
        xUp = event.x
        yUp = event.y
        val swipedHorizontally = abs(xUp - xDown) > swipedThreshold
        view.parent.requestDisallowInterceptTouchEvent(
            swipedHorizontally || (abs(xUp - xDown) > abs(yUp - yDown))
        )
    }

    private fun onActionUp(event: MotionEvent): Boolean {
        xUp = event.x
        yUp = event.y
        val swipedHorizontally = abs(xUp - xDown) > swipedThreshold
        val swipedVertically = abs(yUp - yDown) > swipedThreshold

        val isHorizontalScroll = swipedHorizontally && abs(xUp - xDown) > abs(yUp - yDown)

        if (isHorizontalScroll) {
            val swipedRight = xUp > xDown
            val swipedLeft = xUp < xDown

            if (swipedRight) {
                swipeListener?.onSwipedRight() ?: return false
                return true
            }
            if (swipedLeft) {
                swipeListener?.onSwipedLeft() ?: return false
                return true
            }
        }

        if (!swipedHorizontally && !swipedVertically) {
            when {
                xDown.toInt() in 0..skipAreaDimension -> swipeListener?.onLeftEdgeClick()
                xDown.toInt() in view.width - skipAreaDimension..view.width -> swipeListener?.onRightEdgeClick()
                else -> {
                    requestRipple(event)
                    swipeListener?.onClick()
                }
            }
            return true
        }
        return false
    }

    @SuppressLint("Recycle")
    private fun requestRipple(event: MotionEvent) {
        val downEvent = MotionEvent.obtain(event).apply { this.action = MotionEvent.ACTION_DOWN }
        cover?.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        cover?.dispatchTouchEvent(event)
    }

    fun isTouching(): Flowable<Boolean> = isTouchingPublisher.distinctUntilChanged()


}
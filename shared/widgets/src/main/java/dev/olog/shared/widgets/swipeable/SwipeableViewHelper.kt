package dev.olog.shared.widgets.swipeable

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val isTouchingPublisher = MutableStateFlow(false)

    private val touchSlop by lazy { ViewConfiguration.get(view.context).scaledTouchSlop }

    fun onTouchDown(event: MotionEvent): Boolean {
        view.parent.requestDisallowInterceptTouchEvent(true)
        isTouchingPublisher.value = true
        return onActionDown(event)
    }

    fun onTouchMove(event: MotionEvent): Boolean{
        onActionMove(event)
        isTouchingPublisher.value = true
        return true
    }

    fun onTouchUp(event: MotionEvent): Boolean{
        view.parent.requestDisallowInterceptTouchEvent(false)
        isTouchingPublisher.value = false
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
                xDown.toInt() in 0..skipAreaDimension -> {
                    swipeListener?.onLeftEdgeClick()
                    return true
                }
                xDown.toInt() in view.width - skipAreaDimension..view.width -> {
                    swipeListener?.onRightEdgeClick()
                    return true
                }
                abs(xDown - xUp) < touchSlop && abs(yDown - yUp) < touchSlop -> {
                    requestRipple(event)
                    swipeListener?.onClick()
                    return true
                }
            }
        }
        return false
    }

    @SuppressLint("Recycle")
    private fun requestRipple(event: MotionEvent) {
        // TODO
    }

    fun isTouching(): Flow<Boolean> = isTouchingPublisher


}
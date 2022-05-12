package dev.olog.ui.swipeable

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import dev.olog.ui.R
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.switcher.CustomViewSwitcher
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val viewSwitcher by lazyFast { findViewSwitcher() }

    private val isTouchingPublisher = ConflatedBroadcastChannel(false)

    private val touchSlop by lazy { ViewConfiguration.get(view.context).scaledTouchSlop }

    private fun findViewSwitcher(): CustomViewSwitcher? {
        return (view.parent as ViewGroup).findViewById(R.id.imageSwitcher)
    }

    fun onTouchDown(event: MotionEvent): Boolean {
        view.parent.requestDisallowInterceptTouchEvent(true)
        isTouchingPublisher.trySend(true)
        return onActionDown(event)
    }

    fun onTouchMove(event: MotionEvent): Boolean{
        onActionMove(event)
        isTouchingPublisher.trySend(true)
        return true
    }

    fun onTouchUp(event: MotionEvent): Boolean{
        view.parent.requestDisallowInterceptTouchEvent(false)
        isTouchingPublisher.trySend(false)
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
        val switcher = viewSwitcher ?: return
        val downEvent = MotionEvent.obtain(event).apply { this.action = MotionEvent.ACTION_DOWN }
        val imageView = switcher.getImageView()
        imageView.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        imageView.dispatchTouchEvent(event)
    }

    fun isTouching(): Flow<Boolean> = isTouchingPublisher
        .asFlow()
        .distinctUntilChanged()


}
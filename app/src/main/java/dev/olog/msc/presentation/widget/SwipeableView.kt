package dev.olog.msc.presentation.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.presentation.interfaces.HasSlidingPanel
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.widgets.ForegroundImageView
import dev.olog.shared.extensions.dip
import dev.olog.shared.extensions.findChild
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

private const val DEFAULT_SWIPED_THRESHOLD = 100

class SwipeableView(
        context: Context,
        attrs: AttributeSet
) : View(context, attrs) {

    private val cover by lazyFast { findCover() }

    private val swipedThreshold = DEFAULT_SWIPED_THRESHOLD
    private var xDown = 0f
    private var xUp = 0f
    private var yDown = 0f
    private var yUp = 0f
    private var swipeListener: SwipeListener? = null
    private val isTouchingPublisher = PublishSubject.create<Boolean>()

    private var isTouchEnabled = true

    private val sixtyFourDip by lazy(LazyThreadSafetyMode.NONE) { context.dip(64) }

    private fun findCover() : ForegroundImageView? {
        if (parent is ViewGroup){
            return (parent as ViewGroup).findChild { it is ForegroundImageView } as ForegroundImageView?
        }
        return null
    }

    fun setOnSwipeListener(swipeListener: SwipeListener?) {
        this.swipeListener = swipeListener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode && context is HasSlidingPanel){
            ((context as Activity) as HasSlidingPanel).getSlidingPanel().addPanelSlideListener(slidingPanelListener)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.swipeListener = null
        if (context is HasSlidingPanel){
            ((context as Activity) as HasSlidingPanel).getSlidingPanel().removePanelSlideListener(slidingPanelListener)
        }
    }

    fun isTouching(): Observable<Boolean> = isTouchingPublisher.distinctUntilChanged()

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isTouchingPublisher.onNext(true)
                onActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                onActionMove(event)
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

    private fun onActionMove(event: MotionEvent) {
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
                if (swipeListener != null && isTouchEnabled) {
                    swipeListener!!.onSwipedRight()
                    return true
                }
            }
            if (swipedLeft) {
                if (swipeListener != null && isTouchEnabled) {
                    swipeListener!!.onSwipedLeft()
                    return true
                }
            }
        }

        if (!swipedHorizontally && !swipedVertically) {
            when {
                xDown < sixtyFourDip && isTouchEnabled -> swipeListener?.onLeftEdgeClick()
                ((width - xDown) < sixtyFourDip) && isTouchEnabled-> swipeListener?.onRightEdgeClick()
                else -> {
                    if (isTouchEnabled){
                        requestRipple(event)
                        swipeListener?.onClick()
                    }
                }
            }
            return true
        }
        return false
    }

    @SuppressLint("Recycle")
    private fun requestRipple(event: MotionEvent){
        val downEvent = MotionEvent.obtain(event).apply { this.action = MotionEvent.ACTION_DOWN }
        cover?.dispatchTouchEvent(downEvent)
        downEvent.recycle()
        cover?.dispatchTouchEvent(event)
    }

    private val slidingPanelListener = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            isTouchEnabled = newState == BottomSheetBehavior.STATE_EXPANDED
        }
    }

    interface SwipeListener {
        fun onSwipedLeft() {}
        fun onSwipedRight() {}
        fun onClick() {}
        fun onLeftEdgeClick(){}
        fun onRightEdgeClick(){}
    }
}

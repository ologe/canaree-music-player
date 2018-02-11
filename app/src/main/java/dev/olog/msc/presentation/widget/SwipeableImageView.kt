//package dev.olog.msc.presentation.widget
//
//import android.content.Context
//import android.support.v7.widget.AppCompatImageView
//import android.util.AttributeSet
//import android.view.MotionEvent
//
//private const val DEFAULT_SWIPED_THRESHOLD = 100
//
//class SwipeableImageView @JvmOverloads constructor(
//        context: Context,
//        attrs: AttributeSet? = null,
//        defStyleAttr: Int = 0
//
//) : AppCompatImageView(context, attrs, defStyleAttr) {
//
//    private val swipedThreshold = DEFAULT_SWIPED_THRESHOLD
//    private var xDown = 0f
//    private var xUp = 0f
//    private var yDown = 0f
//    private var yUp = 0f
//    private var swipeListener: SwipeListener? = null
//
//    fun setOnSwipeListener(swipeListener: SwipeListener?) {
//        this.swipeListener = swipeListener
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return when (event.action) {
//            MotionEvent.ACTION_DOWN -> onActionDown(event)
//            MotionEvent.ACTION_UP  -> onActionUp(event)
//            else -> super.onTouchEvent(event)
//        }
//    }
//
//    private fun onActionDown(event: MotionEvent) : Boolean{
//        xDown = event.x
//        yDown = event.y
//        return true
//    }
//
//    private fun onActionUp(event: MotionEvent) : Boolean {
//        xUp = event.x
//        yUp = event.y
//        val swipedHorizontally = Math.abs(xUp - xDown) > swipedThreshold
//        val swipedVertically = Math.abs(yUp - yDown) > swipedThreshold
//
//        val isHorizontalScroll = swipedHorizontally && Math.abs(xUp - xDown) > Math.abs(yUp - yDown)
//
//        if (isHorizontalScroll) {
//            val swipedRight = xUp > xDown
//            val swipedLeft = xUp < xDown
//
//            if (swipedRight) {
//                if (swipeListener != null) {
//                    swipeListener!!.onSwipedRight()
//                    return true
//                }
//            }
//            if (swipedLeft) {
//                if (swipeListener != null) {
//                    swipeListener!!.onSwipedLeft()
//                    return true
//                }
//            }
//        }
//
//        if (!swipedHorizontally && !swipedVertically) {
//            if (swipeListener != null) {
//                swipeListener!!.onClick()
//                return true
//            }
//        }
//        return false
//    }
//
//    interface SwipeListener {
//
//        fun onSwipedLeft() {}
//        fun onSwipedRight() {}
//        fun onClick() {}
//    }
//}

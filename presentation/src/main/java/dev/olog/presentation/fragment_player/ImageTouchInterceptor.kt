package dev.olog.presentation.fragment_player

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import dev.olog.presentation.utils.delegates.weakRef
import dev.olog.presentation.widgets.SwipeableImageView
import org.jetbrains.anko.dip

class ImageTouchInterceptor (
        view: SwipeableImageView

) : RecyclerView.SimpleOnItemTouchListener() {

    private val view by weakRef(view)
    private val swipeThreshold = view.context.dip(64)

    private var downX = 0f
    private var downY = 0f

    private val imageLocation = intArrayOf(0,0)
    private val imageRect = Rect()
    private var imageClicked = false

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        val child = rv.findChildViewUnder(x, y)
        if (child != null){
            // view not visible
            return super.onInterceptTouchEvent(rv, e)
        }

        view.getLocationOnScreen(imageLocation)
        val left = imageLocation[0] + swipeThreshold // reserve space for prev button
        val right = imageLocation[0] + view.width - swipeThreshold // reserve space for next button

        imageRect.set(left, imageLocation[1], right,
                imageLocation[1] + view.height)

        when(e.action){
            MotionEvent.ACTION_DOWN -> {
                if (imageRect.contains(x.toInt(), y.toInt())){
                    imageClicked = true
                    downX = x
                    downY = y
                    view.dispatchTouchEvent(e)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (imageClicked){
                    val xDiff = Math.abs(downX - x)
                    if (xDiff > swipeThreshold){
                        // if is swiping on x axis, disable list scroll
//                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (imageClicked){
                    imageClicked = false
                    val yDiff = Math.abs(downY - y)
                    val xDiff = Math.abs(downX - x)
                    if (yDiff < swipeThreshold && xDiff < swipeThreshold){
                        // notifying click
                        view.onTouchEvent(e)
                        return true
                    } else if (xDiff > swipeThreshold){
                        // notifying swipe
                        view.onTouchEvent(e)
                        return true
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                imageClicked = false
            }
        }

        return super.onInterceptTouchEvent(rv, e)
    }

}
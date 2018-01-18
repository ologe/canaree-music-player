package dev.olog.presentation.fragment_player

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import dev.olog.presentation.utils.delegates.weakRef

private const val DEFAULT_SWIPED_THRESHOLD = 100

class SimpleViewClickInterceptor(
        view: View,
        private val func: () -> Unit

) : RecyclerView.SimpleOnItemTouchListener() {

    private val view by weakRef(view)

    private var downX = 0f
    private var downY = 0f

    private val viewLocation = intArrayOf(0,0)
    private val viewRect = Rect()
    private var clicked = false

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        val child = rv.findChildViewUnder(x, y)
        if (child != null){
            // view not visible
            return super.onInterceptTouchEvent(rv, e)
        }

        view.getLocationOnScreen(viewLocation)

        viewRect.set(viewLocation[0], viewLocation[1],
                viewLocation[0] + view.width,
                viewLocation[1] + view.height)

        when (e.action){
            MotionEvent.ACTION_DOWN -> {
                if (viewRect.contains(x.toInt(), y.toInt())){
                    clicked = true
                    downX = x
                    downY = y
                }
            }
            MotionEvent.ACTION_UP -> {
                if (clicked){
                    clicked = false
                    val swipedHorizontally = Math.abs(downX - x) > DEFAULT_SWIPED_THRESHOLD
                    val swipedVertically = Math.abs(downY - y) > DEFAULT_SWIPED_THRESHOLD
                    if (!swipedHorizontally && !swipedVertically){
                        func()
                        return true
                    }
                }
            }
        }

        return super.onInterceptTouchEvent(rv, e)
    }

}
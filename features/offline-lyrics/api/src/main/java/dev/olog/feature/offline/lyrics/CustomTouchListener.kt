package dev.olog.feature.offline.lyrics

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import java.lang.Math.abs

class CustomTouchListener(
        context: Context,
        private val action: () -> Unit
) : View.OnTouchListener {

    private val configuration = ViewConfiguration.get(context)

    private var timePressed = -1L
    private var xDown = -1f
    private var yDown = -1f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                timePressed = System.currentTimeMillis()
                xDown = event.x
                yDown = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (System.currentTimeMillis() - timePressed <= ViewConfiguration.getTapTimeout()){
                    val xUp = event.x
                    val yUp = event.y
                    if (abs(xUp - xDown) < configuration.scaledTouchSlop &&
                            abs(yUp - yDown) < configuration.scaledTouchSlop){
                        action()
                        return true
                    }
                }
            }
        }
        return false
    }
}
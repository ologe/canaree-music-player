package dev.olog.presentation.fragment_player

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.widget.SeekBar
import dev.olog.presentation.utils.delegates.weakRef

class SeekBarTouchInterceptor (
        seekBar: SeekBar

) : RecyclerView.SimpleOnItemTouchListener() {

    private val seekBar by weakRef(seekBar)
    private val seekBarLocation = intArrayOf(0,0)
    private val seekBarRect = Rect()
    private var seekBarClicked = false

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        val child = rv.findChildViewUnder(x, y)
        if (child != null){
            // view not visible
            return super.onInterceptTouchEvent(rv, e)
        }

        seekBar.getLocationOnScreen(seekBarLocation)
        seekBarLocation[1] = seekBarLocation[1] - seekBar.paddingTop

        seekBarRect.set(seekBarLocation[0],
                seekBarLocation[1],
                seekBarLocation[0] + seekBar.width,
                seekBarLocation[1] + seekBar.height + seekBar.paddingTop)

        when(e.action){
            MotionEvent.ACTION_DOWN -> {
                if (seekBarRect.contains(x.toInt(), y.toInt())){
                    seekBarClicked = true
                    return true
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_MOVE,
            MotionEvent.ACTION_CANCEL -> {
                if (seekBarClicked){
                    seekBarClicked = false
                    return true
                }
            }
        }

        return super.onInterceptTouchEvent(rv, e)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        seekBar.dispatchTouchEvent(e)
    }

}
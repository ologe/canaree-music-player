package dev.olog.msc.presentation.widget.parallax

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.msc.R

class ParallaxRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private val viewLocation = intArrayOf(0, 0)

    private var view: ParallaxImageView? = null

    private var newTranslationY = 0f

    private val listener = OnScrollListener()

    fun setView(view: ParallaxImageView){
        this.view = view
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addOnScrollListener(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnScrollListener(listener)
        this.view = null
    }

    override fun draw(c: Canvas?) {
        getLocationInWindow(viewLocation)
        newTranslationY = top.toFloat() - viewLocation[1].toFloat()
        super.draw(c)
    }

    private inner class OnScrollListener : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            this@ParallaxRecyclerView.view?.onScrollChanged(dy)
        }
    }

}
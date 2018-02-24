package dev.olog.msc.presentation.widget.parallax

import android.content.Context
import android.util.AttributeSet
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.ForegroundImageView

private const val DEFAULT_PARALLAX = .4f

class ParallaxImageView(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    private var parallax : Float

    init {
        val a = context.obtainStyledAttributes(R.styleable.ParallaxView)
        parallax = a.getFloat(R.styleable.ParallaxView_parallax, DEFAULT_PARALLAX)
        a.recycle()
    }

    fun translateY(root: View) {
        translationY = (height - root.bottom).toFloat() * parallax
    }

}
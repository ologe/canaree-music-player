package dev.olog.msc.presentation.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import dev.olog.msc.presentation.utils.CircularOutlineProvider

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageView(context, attrs, defStyleAttr){

    init {
        outlineProvider = CircularOutlineProvider()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable != null && drawable is LayerDrawable){
            val background = (drawable).getDrawable(0) as GradientDrawable
            background.shape = GradientDrawable.OVAL
        }
        super.setImageDrawable(drawable)
    }

}
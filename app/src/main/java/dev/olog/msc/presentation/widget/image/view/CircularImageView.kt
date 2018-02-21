package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import dev.olog.msc.presentation.utils.CircularOutlineProvider
import dev.olog.msc.presentation.widget.ForegroundImageView

class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs){

    init {
        outlineProvider = CircularOutlineProvider()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable != null && drawable is LayerDrawable){
            val background = (drawable).getDrawable(0)
            if (background is GradientDrawable){
                background.shape = GradientDrawable.OVAL
            }
        }
        super.setImageDrawable(drawable)
    }

}
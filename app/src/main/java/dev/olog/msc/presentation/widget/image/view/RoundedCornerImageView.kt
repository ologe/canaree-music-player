package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import org.jetbrains.anko.dip

class RoundedCornerImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0

) : AppCompatImageView(context, attrs, defStyleAttr){

    private val radius = context.dip(5).toFloat()

    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable != null && drawable is LayerDrawable){
            val background = (drawable).getDrawable(0)
            if (background is GradientDrawable){
                background.cornerRadius = radius
            }
        }
        super.setImageDrawable(drawable)
    }

}
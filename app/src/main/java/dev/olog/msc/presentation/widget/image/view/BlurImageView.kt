package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.olog.msc.presentation.widget.image.view.player.AdaptiveColorImageView
import io.alterac.blurkit.BlurKit

class BlurImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null
): AdaptiveColorImageView(context, attrs) {

    private val blurRadius = 20

    override fun setImageBitmap(bm: Bitmap?) {
        if (bm != null){
            super.setImageBitmap(BlurKit.getInstance().blur(bm, blurRadius))
        } else {
            super.setImageBitmap(bm)
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        try {
            if (drawable != null){
                val bm = drawable.toBitmap(drawable.intrinsicWidth / 2, drawable.intrinsicHeight / 2)
                super.setImageDrawable(BlurKit.getInstance().blur(bm, blurRadius).toDrawable(resources))
            } else {
                super.setImageDrawable(drawable)
            }
        } catch (ex: Exception){
            ex.printStackTrace()
            super.setImageDrawable(drawable)
        }
    }

}
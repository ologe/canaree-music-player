package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import dev.olog.msc.R
import dev.olog.msc.presentation.theme.AppTheme
import dev.olog.msc.utils.k.extension.dip

private const val DEFAULT_RADIUS = 5

open class RoundedCornersImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    private val radius : Int

    init {
        val a = context.obtainStyledAttributes(R.styleable.RoundedCornersImageView)
        radius = context.dip(a.getInt(R.styleable.RoundedCornersImageView_imageViewCornerRadius, DEFAULT_RADIUS))
        a.recycle()

        val rounded = AppTheme.isDefault() || AppTheme.isSpotify() || AppTheme.isClean()

        if (rounded){
            val drawable = ContextCompat.getDrawable(context, R.drawable.shape_rounded_corner) as GradientDrawable
            drawable.cornerRadius = context.dip(radius).toFloat()
            background = drawable
            clipToOutline = true
        }
        outlineProvider = RoundedOutlineProvider()
    }

}

private class RoundedOutlineProvider : ViewOutlineProvider() {

    override fun getOutline(view: View, outline: Outline) {
        val corner = view.context.dip(5).toFloat()
        outline.setRoundRect(0 , 0, view.width, view.height, corner)
    }
}
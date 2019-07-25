package dev.olog.presentation.widgets.imageview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShapeCardView(
    context: Context,
    attrs: AttributeSet
) : MaterialCardView(context, attrs) {

    private var job: Job? = null

    private var cachedCornerRadius = -1f

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        val hasImageShape = context.applicationContext as HasImageShape
        job = GlobalScope.launch(Dispatchers.Default) {
            for (imageShape in hasImageShape.observeImageShape()) {
                saveCornerRadius()
                setCornerType(imageShape)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (isInEditMode) {
            return
        }
        job?.cancel()
    }

    private fun saveCornerRadius() {
        if (cachedCornerRadius == -1f) {
            cachedCornerRadius = radius
        }
    }

    private fun setCornerType(imageType: ImageShape) = when (imageType){
        ImageShape.RECTANGLE -> radius = 0f
        ImageShape.ROUND -> radius = cachedCornerRadius
    }

}
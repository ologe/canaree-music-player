package dev.olog.shared.widgets.image

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.viewScope
import dev.olog.shared.lazyFast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ShapeImageView : ShapeableImageView {

    companion object {
        private const val DEFAULT_RADIUS = 5
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


    private val hasImageShape by lazyFast { context.applicationContext.findInContext<HasImageShape>() }
    private val radius: Int = context.dip(DEFAULT_RADIUS)

    init {
        if (isInEditMode) {
            updateShape(ImageShape.ROUND)
        } else {
            updateShape(hasImageShape.getImageShape())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }

        hasImageShape.observeImageShape()
            .onEach {
                updateShape(it)
            }.launchIn(viewScope)
    }

    private fun updateShape(imageShape: ImageShape) {
        val model = ShapeAppearanceModel.Builder()

        when (imageShape) {
            ImageShape.RECTANGLE -> {}
            ImageShape.ROUND -> model
                .setAllCorners(RoundedCornerTreatment())
                .setAllCornerSizes(context.dipf(radius))
            ImageShape.CUT_CORNER -> model
                .setAllCorners(CutCornerTreatment())
                .setAllCornerSizes(context.dipf(radius))
        }
        shapeAppearanceModel = model.build()
    }

}
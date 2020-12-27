package dev.olog.shared.widgets.imageview

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import dev.olog.shared.android.coroutine.viewScope
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.imageShapeAmbient
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ShapeImageView(
    context: Context,
    attrs: AttributeSet
) : ShapeableImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
    }

    init {
        clipToOutline = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        context.imageShapeAmbient.flow
            .onEach(this::setupShape)
            .launchIn(viewScope)
    }

    private fun setupShape(shape: ImageShape) {
        shapeAppearanceModel = buildShapeModel(shape)
    }

    private fun buildShapeModel(imageShape: ImageShape): ShapeAppearanceModel{
        return when (imageShape) {
            ImageShape.RECTANGLE -> buildSquareShape()
            ImageShape.ROUND -> buildRoundedCornerShape()
            ImageShape.CUT_CORNER -> buildCutCornerShape()
        }
    }

    private fun buildSquareShape(): ShapeAppearanceModel {
        return ShapeAppearanceModel()
    }

    private fun buildRoundedCornerShape(): ShapeAppearanceModel {
        return ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(dipf(DEFAULT_RADIUS))
            .build()
    }

    private fun buildCutCornerShape(): ShapeAppearanceModel {
        return ShapeAppearanceModel.Builder()
            .setAllCorners(CutCornerTreatment())
            .setAllCornerSizes(dipf(DEFAULT_RADIUS))
            .build()
    }

}
package dev.olog.shared.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import kotlin.math.max

class CircularImageView(
    context: Context,
    attrs: AttributeSet
) : ShapeableImageView(context, attrs) {

    init {
        shapeAppearanceModel = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes { bounds -> max(bounds.width(), bounds.height()) / 2 } // 50 percent
            .build()

        clipToOutline = true
    }


}
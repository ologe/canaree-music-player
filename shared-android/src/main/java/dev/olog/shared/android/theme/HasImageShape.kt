package dev.olog.shared.android.theme

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.flow.Flow

interface HasImageShape {
    fun getImageShape(): ImageShape
    fun observeImageShape(): Flow<ImageShape>
}

enum class ImageShape(@StringRes val prefValue: Int) {
    RECTANGLE(prefs.R.string.prefs_icon_shape_square),
    ROUND(prefs.R.string.prefs_icon_shape_rounded),
    CUT_CORNER(prefs.R.string.prefs_icon_shape_cut_corner);

    companion object {
        fun fromPref(
            context: Context,
            value: String
        ): ImageShape {
            return values().find { context.getString(it.prefValue) == value } ?: ROUND
        }
    }

    fun shapeAppearance(radius: Float): ShapeAppearanceModel = when (this) {
        RECTANGLE -> ShapeAppearanceModel()
        ROUND -> ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(radius)
            .build()
        CUT_CORNER -> ShapeAppearanceModel.Builder()
            .setAllCorners(CutCornerTreatment())
            .setAllCornerSizes(radius)
            .build()
    }

}


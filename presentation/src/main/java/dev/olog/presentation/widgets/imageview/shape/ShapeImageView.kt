package dev.olog.presentation.widgets.imageview.shape

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.shape.CutCornerTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import dev.olog.feature.presentation.base.extensions.dipf
import dev.olog.presentation.R
import dev.olog.shared.android.theme.themeManager
import dev.olog.shared.android.theme.ImageShape
import dev.olog.feature.presentation.base.widget.ForegroundImageView

open class ShapeImageView(
    context: Context,
    attrs: AttributeSet

) : ForegroundImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
        @JvmStatic
        private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val radius: Int
    private var mask: Bitmap? = null
        get() {
            if (field == null) {
                val shape = context.themeManager.imageShape
                field = buildMaskShape(getShapeModel(shape))
            }
            return field
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val cutCornerShapeModel: ShapeAppearanceModel
    private val roundedShapeModel: ShapeAppearanceModel
    private val squareShapeModel: ShapeAppearanceModel

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornersImageView)
        radius = a.getInt(
            R.styleable.RoundedCornersImageView_imageViewCornerRadius,
            DEFAULT_RADIUS
        )
        a.recycle()

        clipToOutline = true

        paint.xfermode = X_FERMO_MODE

        cutCornerShapeModel = ShapeAppearanceModel.Builder()
            .setAllCorners(CutCornerTreatment())
            .setAllCornerSizes(context.dipf(radius))
            .build()

        roundedShapeModel = ShapeAppearanceModel.Builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(context.dipf(radius))
            .build()

        squareShapeModel = ShapeAppearanceModel()

        if (!isInEditMode) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
            val shape = context.themeManager.imageShape
            updateBackground(getShapeModel(shape))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInEditMode) {
            mask?.let {
                canvas.drawBitmap(it, 0f, 0f, paint)
            }
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        mask = null
    }

    private fun getShapeModel(imageShape: ImageShape): ShapeAppearanceModel {
        return when (imageShape) {
            ImageShape.ROUND -> roundedShapeModel
            ImageShape.CUT_CORNER -> cutCornerShapeModel
            ImageShape.RECTANGLE -> squareShapeModel
        }
    }

    private fun buildMaskShape(shape: ShapeAppearanceModel): Bitmap? {
        if (width > 0 && height > 0) {
            val drawable = MaterialShapeDrawable(shape)
            return drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
        } else {
            return null
        }
    }

    private fun updateBackground(shape: ShapeAppearanceModel) {
        val drawable = MaterialShapeDrawable(shape)
        background = drawable
    }

}
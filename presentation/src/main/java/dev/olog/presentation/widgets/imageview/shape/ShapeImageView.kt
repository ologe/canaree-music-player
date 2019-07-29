package dev.olog.presentation.widgets.imageview.shape

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.shape.*
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.ForegroundImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class ShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null

) : ForegroundImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
        @JvmStatic
        private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val hasImageShape by lazyFast { context.applicationContext as HasImageShape }

    private var job: Job? = null

    private val radius: Int
    private var mask: Bitmap? = null
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

        paint.xfermode =
            X_FERMO_MODE

        cutCornerShapeModel = ShapeAppearanceModel().apply {
            setAllCorners(CutCornerTreatment(context.dipf(radius)))
        }
        roundedShapeModel = ShapeAppearanceModel().apply {
            setAllCorners(RoundedCornerTreatment(context.dipf(radius)))
        }
        squareShapeModel = ShapeAppearanceModel()

        ShapeAppearanceModel()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val hasImageShape = context.applicationContext as HasImageShape
        job = GlobalScope.launch(Dispatchers.Default) {
            for (imageShape in hasImageShape.observeImageShape()) {
                mask = null
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isInEditMode) {
            getMask()?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
        }
    }

    private fun getMask(): Bitmap? {
        if (mask == null) {
            mask = when (hasImageShape.getImageShape()) {
                ImageShape.ROUND -> buildMaskShape(roundedShapeModel)
                ImageShape.CUT_CORNER -> buildMaskShape(cutCornerShapeModel)
                ImageShape.RECTANGLE -> buildMaskShape(squareShapeModel)
            }
        }
        return mask
    }

    private fun buildMaskShape(shape: ShapeAppearanceModel): Bitmap{
        val drawable = MaterialShapeDrawable(shape)
        return drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
    }

}
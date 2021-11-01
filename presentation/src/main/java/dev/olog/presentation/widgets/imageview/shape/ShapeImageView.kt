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
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.dipf
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.android.theme.HasImageShape
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.ForegroundImageView
import kotlinx.coroutines.*

open class ShapeImageView(
    context: Context,
    attrs: AttributeSet

) : ForegroundImageView(context, attrs) {

    companion object {
        private const val DEFAULT_RADIUS = 5
        @JvmStatic
        private val X_FERMO_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val hasImageShape by lazyFast { context.applicationContext.findInContext<HasImageShape>() }

    private var job: Job? = null

    private val radius: Int
    private var mask: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val cutCornerShapeModel: ShapeAppearanceModel
    private val roundedShapeModel: ShapeAppearanceModel
    private val squareShapeModel: ShapeAppearanceModel

    init {
        val a = context.obtainStyledAttributes(attrs, dev.olog.shared.widgets.R.styleable.RoundedCornersImageView)
        radius = a.getInt(
            dev.olog.shared.widgets.R.styleable.RoundedCornersImageView_imageViewCornerRadius,
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
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val hasImageShape = context.applicationContext.findInContext<HasImageShape>()
        job = GlobalScope.launch(Dispatchers.Default) {
            for (imageShape in hasImageShape.observeImageShape()) {
                mask = null
                updateBackground(getShapeModel(imageShape))
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
            getMask()?.let {
                canvas.drawBitmap(it, 0f, 0f, paint)
            }
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        mask = null
    }

    private fun getMask(): Bitmap? {
        if (mask == null) {
            mask = buildMaskShape(getShapeModel(hasImageShape.getImageShape()))
        }
        return mask
    }

    private fun getShapeModel(imageShape: ImageShape): ShapeAppearanceModel{
        return when (imageShape) {
            ImageShape.ROUND -> roundedShapeModel
            ImageShape.CUT_CORNER -> cutCornerShapeModel
            ImageShape.RECTANGLE -> squareShapeModel
        }
    }

    private fun buildMaskShape(shape: ShapeAppearanceModel): Bitmap? {
        if (width > 0 && height > 0){
            val drawable = MaterialShapeDrawable(shape)
            return drawable.toBitmap(width, height, Bitmap.Config.ALPHA_8)
        } else {
            return null
        }
    }

    private suspend fun updateBackground(shape: ShapeAppearanceModel) = withContext(Dispatchers.Main){
        val drawable = MaterialShapeDrawable(shape)
        background = drawable
    }

}
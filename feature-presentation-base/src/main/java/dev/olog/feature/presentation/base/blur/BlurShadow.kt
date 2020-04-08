package dev.olog.feature.presentation.base.blur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView

object BlurShadow {

    private const val DOWNSCALE_FACTOR = 0.2f
    private const val DEFAULT_RADIUS_OFFSET = 0.5f // or .85?
    const val RADIUS = 7f * 2 * DEFAULT_RADIUS_OFFSET

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    private val script by lazy {
        ScriptIntrinsicBlur.create(
            renderScript, Element.U8_4(
                renderScript
            )
        ).apply { setRadius(RADIUS) }
    }

    fun blur(view: ImageView, width: Int, height: Int): Bitmap? {
        val src = getBitmapForView(
            view,
            width,
            height
        ) ?: return null
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)

        script.apply {
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            (width * DOWNSCALE_FACTOR).toInt(),
            (height * DOWNSCALE_FACTOR).toInt(),
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(
            DOWNSCALE_FACTOR,
            DOWNSCALE_FACTOR
        )
        canvas.setMatrix(matrix)
        view.draw(canvas)
        return bitmap
    }
}
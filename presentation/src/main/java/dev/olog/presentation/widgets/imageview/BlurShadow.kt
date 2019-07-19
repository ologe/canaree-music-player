package dev.olog.presentation.widgets.imageview

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

    private var renderScript: RenderScript? = null

    fun init(context: Context) {
        if (renderScript == null)
            renderScript = RenderScript.create(context)
    }

    fun blur(view: ImageView, width: Int, height: Int, radius: Float): Bitmap? {
        val src = getBitmapForView(
            view,
            PlayerShadowImageView.DOWNSCALE_FACTOR,
            width,
            height
        ) ?: return null
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(
            renderScript, Element.U8_4(
                renderScript
            )
        )
        script.apply {
            setRadius(radius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, downscaleFactor: Float, width: Int, height: Int): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            (width * downscaleFactor).toInt(),
            (height * downscaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)
        canvas.setMatrix(matrix)
        view.draw(canvas)
        return bitmap
    }
}
package dev.olog.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

object ImageUtils {

    fun joinImages(list: List<Bitmap>) : Bitmap {
        val result = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        list.forEachIndexed { i, bitmap ->
            val bit = Bitmap.createScaledBitmap(bitmap, 250, 250, false)
            canvas.drawBitmap(bit, (250 * (i % 2)).toFloat(), (250 * (i / 2)).toFloat(), paint)
            bitmap.recycle()
            bit.recycle()
        }

        return result
    }

}
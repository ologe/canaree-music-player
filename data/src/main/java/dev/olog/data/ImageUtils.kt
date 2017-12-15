package dev.olog.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import dev.olog.data.utils.assertBackgroundThread

object ImageUtils {

    fun joinImages(list: List<Bitmap>) : Bitmap {
        assertBackgroundThread()

        val resultList = when (list.size){
            1 -> {
                val item = list[0]
                listOf(item, item, item, item)
            }
            2 -> {
                val item1 = list[0]
                val item2 = list[1]
                listOf(item1, item2, item2, item1)
            }
            3 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                listOf(item1, item2, item3, item1)
            }
            else -> list // case 4
        }

        val result = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        resultList.forEachIndexed { i, bitmap ->
            val bit = Bitmap.createScaledBitmap(bitmap, 250, 250, false)
            canvas.drawBitmap(bit, (250 * (i % 2)).toFloat(), (250 * (i / 2)).toFloat(), paint)
            bit.recycle()
        }

        return result
    }

}
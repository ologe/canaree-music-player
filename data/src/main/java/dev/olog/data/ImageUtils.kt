package dev.olog.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import dev.olog.data.utils.assertBackgroundThread

object ImageUtils {

    private const val IMAGE_SIZE = 750

    fun joinImages(list: List<Bitmap>) : Bitmap {
        assertBackgroundThread()

        val resultList = when {
            list.size == 1 -> {
                val item = list[0]
                listOf(item, item, item, item)
            }
            list.size == 2 -> {
                val item1 = list[0]
                val item2 = list[1]
                listOf(item1, item2, item2, item1)
            }
            list.size < 9 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                listOf(item1, item2, item3, item1)
            }
            else -> list // case 4
        }

        val combinedImage = create(resultList, IMAGE_SIZE, if (list.size == 9) 3 else 2)
        val rotatedBitmap = rotate(combinedImage, IMAGE_SIZE, if (list.size == 9) 9f else 3f)
//        val croppedBitmap = centerCrop(rotatedBitmap, IMAGE_SIZE, list.size)
//        rotatedBitmap.recycle()

        return rotatedBitmap
    }

    private fun create(images: List<Bitmap>, imageSize: Int, parts: Int) : Bitmap {
        val result = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()
        val padding = 10
        val onePartSize = imageSize / parts
        images.forEachIndexed { i, bitmap ->
            val bit = Bitmap.createScaledBitmap(bitmap, onePartSize - padding, onePartSize - padding, false)
            canvas.drawBitmap(bit, (onePartSize * (i % parts)).toFloat(), (onePartSize * (i / parts)).toFloat(), paint)
            bit.recycle()
        }
        return result
    }

    private fun rotate(bitmap: Bitmap, imageSize: Int, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.setTranslate((- IMAGE_SIZE / 2).toFloat(), (- IMAGE_SIZE / 2).toFloat())
        matrix.postRotate(degrees)
        matrix.postTranslate((IMAGE_SIZE / 2).toFloat(), (IMAGE_SIZE / 2).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, imageSize, imageSize, matrix, true)
    }

    private fun centerCrop(bitmap: Bitmap, imageSize: Int, listSize: Int): Bitmap {
        val point = if (listSize == 9){
            imageSize / 3 / 3
        } else imageSize / 2 / 4

        return Bitmap.createBitmap(bitmap, point, point, imageSize - point * 2, imageSize - point * 2)
    }

}
package dev.olog.image.provider.merger

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.shared.android.extensions.dip
import javax.inject.Inject

internal class ImageMerger @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val IMAGE_SIZE = 1600
        const val PARTS = 9
        private const val ROW_PARTS = 3
        private const val SCALE = 1.7f
        private const val DEGREES = 9f
        private const val LINE_STROKE = 2.5f
    }

    fun execute(drawables: List<Drawable>) : Bitmap {
        val arranged = arrange(drawables.shuffled())

        return create(
            drawables = arranged,
            imageSize = IMAGE_SIZE,
            parts = ROW_PARTS
        )
    }

    private fun arrange(list: List<Drawable>): List<Drawable> {
        return when {
            list.size == 1 -> {
                val item = list[0]
                listOf(item, item, item, item, item, item, item, item, item)
            }
            list.size == 2 -> {
                val item1 = list[0]
                val item2 = list[1]
                listOf(item1, item2, item1, item2, item1, item2, item1, item2, item1)
            }
            list.size == 3 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                listOf(item1, item2, item3, item3, item1, item2, item2, item3, item1)
            }
            list.size == 4 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                val item4 = list[3]
                listOf(item1, item2, item3, item4, item1, item2, item3, item4, item1)
            }
            list.size < 9 -> { // 5 to 8
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                val item4 = list[3]
                val item5 = list[4]
                listOf(item1, item2, item3, item4, item5, item2, item3, item4, item1)
            }
            else -> list // case 9
        }
    }

    @Suppress("SameParameterValue")
    private fun create(drawables: List<Drawable>, imageSize: Int, parts: Int) : Bitmap {
        val result = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val onePartSize = imageSize / parts

        val center = PointF(imageSize / 2f, imageSize / 2f)
        canvas.rotate(DEGREES, center.x, center.y)
        canvas.scale(SCALE, SCALE, center.x, center.y)

        for ((i, drawable) in drawables.withIndex()) {
            canvas.save()
            canvas.translate((onePartSize * (i % parts)).toFloat(), (onePartSize * (i / parts)).toFloat())

            val mutated = drawable.mutate()
            mutated.setBounds(0, 0, onePartSize, onePartSize)
            mutated.draw(canvas)
            canvas.restore()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.strokeWidth = context.dip(LINE_STROKE).toFloat()

        for (position in 0 until IMAGE_SIZE step onePartSize) {
            canvas.drawLine(position.toFloat(), 0f, position.toFloat(), imageSize.toFloat(), paint)
            canvas.drawLine(0f, position.toFloat(), imageSize.toFloat(), position.toFloat(), paint)
        }

        return result
    }

}
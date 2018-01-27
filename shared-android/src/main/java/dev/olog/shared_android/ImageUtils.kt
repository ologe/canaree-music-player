package dev.olog.shared_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.InputStream

object ImageUtils {

    fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap: Bitmap

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        println("intrinsicWidth ${drawable.intrinsicWidth}, height ${drawable.intrinsicHeight}")

        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.height, canvas.width)
        drawable.draw(canvas)
        return bitmap
    }

    fun getBitmapFromUriOrNull(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            decodeSampledBitmap(context, uri, reqWidth, reqHeight)
        } catch (ex: Exception) {
            null
        }
    }

    fun getBitmapFromUriWithPlaceholder(context: Context, uri: Uri, id: Long, reqWidth: Int, reqHeight: Int): Bitmap {
        return try {
            decodeSampledBitmap(context, uri, reqWidth, reqHeight)
        } catch (ex: Exception) {
            getPlaceholderAsBitmap(context, id)
        }
    }

    private fun getPlaceholderAsBitmap(context: Context, id: Long): Bitmap {
        val size = 200
        val drawable = CoverUtils.getGradientForNotification(context, id)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        return bitmap
    }

    fun decodeSampledBitmap(
            context: Context, url: Uri, reqWidth: Int, reqHeight: Int): Bitmap {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val input = openStream(context, url)
        BitmapFactory.decodeStream(input, null, options)
        input.close()

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false

        val input2 = openStream(context, url)
        val bitmap = BitmapFactory.decodeStream(input2, null, options)
        input2.close()
        return bitmap
    }

    private fun openStream(context: Context, uri: Uri): InputStream {
        return context.contentResolver.openInputStream(uri)
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {

        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


}
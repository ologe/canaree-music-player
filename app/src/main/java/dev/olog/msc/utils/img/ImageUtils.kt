package dev.olog.msc.utils.img

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.URLUtil
import java.io.File
import java.io.InputStream

object ImageUtils {

    fun createNotificationImage(context: Context, image: String, id: Int){
        val uri = Uri.fromFile(File(image))
        val isUrl = when {
            URLUtil.isNetworkUrl(image) -> true
            else -> false
        }

        if (isUrl){

        }

    }


    fun getBitmap(context: Context, uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        return try {
            decodeSampledBitmap(context, uri, reqWidth, reqHeight)
        } catch (ex: Exception) {
            null
        }
    }

    private fun decodeSampledBitmap(
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
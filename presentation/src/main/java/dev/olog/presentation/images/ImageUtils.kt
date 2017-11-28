package dev.olog.presentation.images

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore

object ImageUtils {

    fun getBitmapFromUri(context: Context, coverUri: String?, source: Int, position: Int): Bitmap? {
        if (coverUri == null){
            return null
        }

        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(coverUri))
        } catch (ex: Exception) {
            getPlaceholderAsBitmap(context, source, position)
        }
    }


    private fun getPlaceholderAsBitmap(context: Context, source: Int, position: Int): Bitmap? {
        return try {
            val drawable = CoverUtils.getGradient(context, position = position, source = source)

            val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, 24, 24)
            drawable.draw(canvas)

            bitmap
        } catch (ex: Exception){
            null
        }
    }

}
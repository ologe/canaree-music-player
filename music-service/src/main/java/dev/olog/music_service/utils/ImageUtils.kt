package dev.olog.music_service.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore

object ImageUtils {

    fun getBitmapFromUriWithPlaceholder(context: Context, uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (ex: Exception) {
            getPlaceholderAsBitmap(context)
        }
    }

    private fun getPlaceholderAsBitmap(context: Context): Bitmap? {
        return try {
            val drawable = CoverUtils.getGradient(context, 3)

            val bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, 24, 24)
            drawable.draw(canvas)

            return bitmap
        } catch (ex: Exception){
            return null
        }

    }

    fun getBitmapFromUri(context: Context, coverUri: String?): Bitmap? {
        if (coverUri == null){
            return null
        }

        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(coverUri))
        } catch (ex: Exception) {
            null
        }

    }

}
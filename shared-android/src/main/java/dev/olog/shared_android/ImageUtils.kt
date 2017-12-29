package dev.olog.shared_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore
import org.jetbrains.anko.dip

object ImageUtils {

    fun getBitmapFromUri(context: Context, uri: String?): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(uri))
        } catch (ex: Exception) {
            null
        }
    }

    fun getBitmapFromUriWithPlaceholder(context: Context, uri: Uri, id: Long): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (ex: Exception) {
            getPlaceholderAsBitmap(context, id)
        }
    }

    private fun getPlaceholderAsBitmap(context: Context, id: Long): Bitmap {
        val size = context.dip(128)
        val drawable = CoverUtils.getGradientForNotification(context, id)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)

        return bitmap
    }

}
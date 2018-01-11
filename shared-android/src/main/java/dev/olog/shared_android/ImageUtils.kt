package dev.olog.shared_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.dip

object ImageUtils {

    fun getBitmapFromDrawable(context: Context, @DrawableRes drawableRes: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        return getBitmapFromDrawable(drawable!!)
    }

    fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap: Bitmap

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.height, canvas.width)
        drawable.draw(canvas)
        return bitmap
    }

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
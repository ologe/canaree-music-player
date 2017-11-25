package dev.olog.presentation.images

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

object ImageUtils {

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
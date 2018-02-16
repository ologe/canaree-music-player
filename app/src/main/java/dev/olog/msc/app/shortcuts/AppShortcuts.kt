package dev.olog.msc.app.shortcuts

import android.content.Context
import android.graphics.*
import android.net.Uri
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImageUtils
import java.io.File

interface AppShortcuts {

    fun disablePlay()
    fun enablePlay()

    fun addDetailShortcut(mediaId: MediaId, title: String, image: String)

    fun getBitmap(context: Context, mediaId: MediaId, image: String): Bitmap {
        val id = if (mediaId.isFolder) mediaId.categoryValue.hashCode().toLong()
                else mediaId.categoryValue.toLong()

        val file = File(image)
        val uri = if (file.exists()){
            Uri.fromFile(file)
        } else {
            Uri.parse(image)
        }

        val bitmap = ImageUtils.getBitmapFromUriWithPlaceholder(context, uri, id, 128, 128)
        return drawRounded(bitmap)
    }

    private fun drawRounded(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawARGB(0,0,0,0)
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
                (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

}
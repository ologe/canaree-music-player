package dev.olog.msc.app.shortcuts

import android.content.Context
import android.graphics.Bitmap
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

        return ImageUtils.getBitmapFromUriWithPlaceholder(context, uri, id, 128, 128)
    }

}
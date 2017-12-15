package dev.olog.data.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun saveFile(context: Context, parentFolder: String, fileName: String, bitmap: Bitmap): String {
        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parentFolder")
        parentFile.mkdirs()
        val dest = File(parentFile, fileName)
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.close()
        bitmap.recycle()
        return dest.path
    }

    fun playlistImagePath(context: Context, playlistId: Long): String {
        return "${context.applicationInfo.dataDir}${File.separator}playlist${File.separator}$playlistId"
    }

    fun

}
package dev.olog.data.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    fun saveFile(context: Context, parent: String, fileName: String, bitmap: Bitmap): String {
        val parentFile = File("${context.applicationInfo.dataDir}${File.separator}$parent")
        parentFile.mkdirs()
        val dest = File(parentFile, fileName)
        val out = FileOutputStream(dest)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.close()
        bitmap.recycle()
        return dest.path
    }

}
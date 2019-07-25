@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.os.Environment
import android.webkit.MimeTypeMap
import java.io.File

inline fun File.isStorageDir(): Boolean {
    return this == Environment.getExternalStorageDirectory()
}

fun File.isAudioFile(): Boolean {
    return fileIsMimeType("audio/*", MimeTypeMap.getSingleton())  ||
            fileIsMimeType("application/ogg", MimeTypeMap.getSingleton())
}

private fun File.fileIsMimeType(mimeType: String?, mimeTypeMap: MimeTypeMap): Boolean {
    if (mimeType == null || mimeType == "*/*") {
        return true
    } else {
        // get the file mime type
        val filename = this.toURI().toString()
        val dotPos = filename.lastIndexOf('.')
        if (dotPos == -1) {
            return false
        }
        val fileExtension = filename.substring(dotPos + 1).toLowerCase()
        val fileType = mimeTypeMap.getMimeTypeFromExtension(fileExtension) ?: return false
        // check the 'type/subtype' pattern
        if (fileType == mimeType) {
            return true
        }
        // check the 'type/*' pattern
        val mimeTypeDelimiter = mimeType.lastIndexOf('/')
        if (mimeTypeDelimiter == -1) {
            return false
        }
        val mimeTypeMainType = mimeType.substring(0, mimeTypeDelimiter)
        val mimeTypeSubtype = mimeType.substring(mimeTypeDelimiter + 1)
        if (mimeTypeSubtype != "*") {
            return false
        }
        val fileTypeDelimiter = fileType.lastIndexOf('/')
        if (fileTypeDelimiter == -1) {
            return false
        }
        val fileTypeMainType = fileType.substring(0, fileTypeDelimiter)
        if (fileTypeMainType == mimeTypeMainType) {
            return true
        }
    }
    return false
}

fun File.safeGetCanonicalPath(): String{
    try {
        return canonicalPath
    } catch (e: Exception) {
        e.printStackTrace()
        return absolutePath
    }

}

fun File.safeGetCanonicalFile(): File{
    try {
        return canonicalFile
    } catch (e: Exception) {
        e.printStackTrace()
        return absoluteFile
    }

}
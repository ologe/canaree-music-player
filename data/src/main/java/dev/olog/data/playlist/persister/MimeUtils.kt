package dev.olog.data.playlist.persister

import android.content.ClipDescription

import android.webkit.MimeTypeMap
import java.io.File
import java.util.*

object MimeUtils {

    fun resolveMimeType(file: File): String {
        val extension: String = file.extension
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase(Locale.ROOT))
            ?: return ClipDescription.MIMETYPE_UNKNOWN
    }

}
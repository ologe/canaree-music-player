package dev.olog.msc.data.repository.util

import android.content.ContentResolver
import android.net.Uri

object CommonQuery {

    fun getSize(contentResolver: ContentResolver, uri: Uri): Int {
        try {
            val cursor = contentResolver.query(uri, arrayOf("count(*)"), null,
                    null, null)

            var size = 0
            cursor?.use {
                it.moveToFirst()
                size = cursor.getInt(0)
            }

            return size
        } catch (ex: IllegalArgumentException) {
            return 0
        }
    }

}
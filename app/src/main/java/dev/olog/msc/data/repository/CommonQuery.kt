package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

object CommonQuery {

    fun getSize(contentResolver: ContentResolver, uri: Uri): Int {

        val cursor = contentResolver.query(uri, arrayOf("count(*)"), null,
                null, null)

        cursor.moveToFirst()
        val size = cursor.getInt(0)
        cursor.close()

        return size

    }

    fun extractAlbumIdsFromSongs(contentResolver: ContentResolver, uri: Uri): List<Long> {
        val result = mutableListOf<Long>()

        val cursor = contentResolver.query(
                uri, arrayOf(MediaStore.Audio.Playlists.Members.ALBUM_ID), null,
                null, null)
        while (cursor.moveToNext()){
            result.add(cursor.getLong(0))
        }
        cursor.close()
        return result
    }

}
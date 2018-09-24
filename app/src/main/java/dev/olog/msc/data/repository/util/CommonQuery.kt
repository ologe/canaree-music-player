package dev.olog.msc.data.repository.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.SparseArray
import dev.olog.msc.app.app
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import java.io.File

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

        val projection = arrayOf(MediaStore.Audio.Playlists.Members.ALBUM_ID)

        val cursor = contentResolver.query(
                uri, projection, null,
                null, null)
        while (cursor.moveToNext()){
            result.add(cursor.getLong(0))
        }
        cursor.close()
        return result
    }

    fun getAllSongsIdNotBlackListd(
            contentResolver: ContentResolver,
            appPreferencesUseCase: AppPreferencesUseCase): List<Long> {

        val list = mutableListOf<Pair<Long, String>>()
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA),
                "${MediaStore.Audio.Media.IS_PODCAST} = 0", null, null)
        while (cursor.moveToNext()){
            list.add(cursor.getLong(0) to cursor.getString(1))
        }
        cursor.close()

        return removeBlacklisted(appPreferencesUseCase.getBlackList(), list)
    }

    private fun removeBlacklisted(blackList: Set<String>, original: List<Pair<Long, String>>): List<Long>{
        return original
                .asSequence()
                .filter {
                    val folderPth = it.second.substring(0, it.second.lastIndexOf(File.separator))
                    !blackList.contains(folderPth)
                }
                .map { it.first }
                .toList()
    }

    fun searchForImages(): SparseArray<String> {
        val cursor = app.contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART), null,
                null, MediaStore.Audio.Albums._ID)

        val result = SparseArray<String>()

        while (cursor.moveToNext()){
            val albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
            if (albumArt != null){
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
                result.append(id, albumArt)
            }
        }

        cursor.close()

        return result
    }

}
package dev.olog.msc.data.repository.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.SparseArray
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.utils.getInt
import dev.olog.msc.utils.getStringOrNull
import java.io.File

object CommonQuery {

    fun getSize(contentResolver: ContentResolver, uri: Uri): Int {

        val cursor = contentResolver.query(uri, arrayOf("count(*)"), null,
                null, null)

        var size = 0
        cursor?.use {
            it.moveToFirst()
            size = cursor.getInt(0)
        }

        return size

    }

    fun extractAlbumIdsFromSongs(contentResolver: ContentResolver, uri: Uri): List<Long> {
        val result = mutableListOf<Long>()

        val projection = arrayOf(MediaStore.Audio.Playlists.Members.ALBUM_ID)

        val cursor = contentResolver.query(uri, projection, null,
                null, null)

        cursor?.use {
            while (cursor.moveToNext()){
                result.add(it.getLong(0))
            }
        }

        return result
    }

    fun getAllSongsIdNotBlackListd(
            contentResolver: ContentResolver,
            appPreferencesUseCase: AppPreferencesGateway): List<Long> {

        val list = mutableListOf<Pair<Long, String>>()
        val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(BaseColumns._ID, MediaStore.MediaColumns.DATA),
                "${MediaStore.Audio.Media.IS_PODCAST} = 0", null, null)

        cursor?.use {
            while (it.moveToNext()){
                list.add(it.getLong(0) to it.getString(1))
            }
        }

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

    fun searchForImages(context: Context): SparseArray<String> {
        val cursor = context.contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART), null,
                null, MediaStore.Audio.Albums._ID)

        val result = SparseArray<String>()

        cursor?.use {
            while (it.moveToNext()){
                val albumArt = it.getStringOrNull(MediaStore.Audio.Albums.ALBUM_ART)
                if (albumArt != null){
                    val id = cursor.getInt(MediaStore.Audio.Albums._ID)
                    result.append(id, albumArt)
                }
            }
        }

        return result
    }

}
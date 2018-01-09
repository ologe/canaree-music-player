package dev.olog.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.data.db.AppDatabase
import dev.olog.shared_android.Constants
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

class PlaylistRepositoryHelper @Inject constructor(
        private val contentResolver: ContentResolver,
        appDatabase: AppDatabase

){

    private val historyDao = appDatabase.historyDao()
    private val favoriteDao = appDatabase.favoriteDao()

    fun createPlaylist(playlistName: String): Single<Long> {
        return Single.create<Long> { e ->
            val added = System.currentTimeMillis()

            val contentValues = ContentValues()
            contentValues.put(MediaStore.Audio.Playlists.NAME, playlistName)
            contentValues.put(MediaStore.Audio.Playlists.DATE_ADDED, added)
            contentValues.put(MediaStore.Audio.Playlists.DATE_MODIFIED, added)

            try {
                val uri = contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues)

                e.onSuccess(ContentUris.parseId(uri))

            } catch (exception: Exception){
                e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
    }

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Single<String> {
        return Single.create<String> { e ->

            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            val cursor = contentResolver.query(uri, arrayOf("max(${MediaStore.Audio.Playlists.Members.PLAY_ORDER})"),
                    null, null, null)

            var itemInserted = 0

            cursor.use {
                if (cursor.moveToFirst()){
                    var maxId = it.getInt(0) + 1

                    val arrayOf = mutableListOf<ContentValues>()
                    for (songId in songIds) {
                        val values = ContentValues(2)
                        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, maxId++)
                        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId)
                        arrayOf.add(values)
                    }

                    itemInserted = contentResolver.bulkInsert(uri, arrayOf.toTypedArray())

                    contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)

                } else {
                    e.onError(IllegalArgumentException("invalid playlist id $playlistId"))
                }
            }

            e.onSuccess(itemInserted.toString())
        }
    }

    fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable{
            contentResolver.delete(
                    MEDIA_STORE_URI,
                    "${BaseColumns._ID} = ?",
                    arrayOf("$playlistId"))
        }
    }

    fun clearPlaylist(playlistId: Long){
        if (Constants.autoPlaylists.contains(playlistId)){
            when (playlistId){
                Constants.FAVORITE_LIST_ID -> favoriteDao.deleteAll()
                Constants.HISTORY_LIST_ID -> historyDao.deleteAll()
            }
            return
        }
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        contentResolver.delete(uri, null, null)
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long): Completable {
        return Completable.create { e ->
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
            contentResolver.delete(uri, "${MediaStore.Audio.Playlists.Members._ID} = ?", arrayOf("$songId"))

            e.onComplete()
        }
    }

    fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.create { e ->

            val values = ContentValues(1)
            values.put(MediaStore.Audio.Playlists.NAME, newTitle)

            val rowsUpdated = contentResolver.update(MEDIA_STORE_URI,
                    values, "${BaseColumns._ID} = ?", arrayOf("$playlistId"))

            if (rowsUpdated > 0){
                e.onComplete()
            } else {
                e.onError(Throwable("playlist name not updated"))
            }

        }.subscribeOn(Schedulers.io())
    }

    fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return MediaStore.Audio.Playlists.Members.moveItem(contentResolver, playlistId, from, to)
    }

}
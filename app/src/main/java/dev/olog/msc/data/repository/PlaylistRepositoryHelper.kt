package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.provider.BaseColumns
import android.provider.MediaStore
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.domain.gateway.FavoriteGateway
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

class PlaylistRepositoryHelper @Inject constructor(
        private val contentResolver: ContentResolver,
        appDatabase: AppDatabase,
        private val favoriteGateway: FavoriteGateway

){

    private val historyDao = appDatabase.historyDao()

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
        }
    }

    fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        val cursor = contentResolver.query(uri, arrayOf("max(${MediaStore.Audio.Playlists.Members.PLAY_ORDER})"),
                null, null, null)

        if (cursor.moveToFirst()) {
            var maxId = cursor.getInt(0) + 1

            val arrayOf = mutableListOf<ContentValues>()
            for (songId in songIds) {
                val values = ContentValues(2)
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, maxId++)
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songId)
                arrayOf.add(values)
            }

            contentResolver.bulkInsert(uri, arrayOf.toTypedArray())
            contentResolver.notifyChange(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null)
        }
        cursor.close()
    }

    fun deletePlaylist(playlistId: Long): Completable {
        return Completable.fromCallable{
            contentResolver.delete(MEDIA_STORE_URI, "${BaseColumns._ID} = ?", arrayOf("$playlistId"))
        }
    }

    fun clearPlaylist(playlistId: Long){
        if (PlaylistConstants.isAutoPlaylist(playlistId)){
            when (playlistId){
                PlaylistConstants.FAVORITE_LIST_ID -> favoriteGateway.deleteAll()
                PlaylistConstants.HISTORY_LIST_ID -> historyDao.deleteAll()
            }
            return
        }
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
        contentResolver.delete(uri, null, null)
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long): Completable {
        return Completable.create { e ->
            if (PlaylistConstants.isAutoPlaylist(playlistId)){
                removeFromAutoPlaylist(playlistId, songId)
            } else {
                val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)
                contentResolver.delete(uri, "${MediaStore.Audio.Playlists.Members._ID} = ?", arrayOf("$songId"))
            }

            e.onComplete()
        }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long){
        when(playlistId){
            PlaylistConstants.FAVORITE_LIST_ID -> favoriteGateway.deleteSingle(songId)
            PlaylistConstants.HISTORY_LIST_ID -> historyDao.deleteSingle(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
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
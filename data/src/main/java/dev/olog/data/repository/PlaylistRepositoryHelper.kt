package dev.olog.data.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore.Audio.Playlists
import dev.olog.contentresolversql.querySql
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.getLong
import javax.inject.Inject

internal class PlaylistRepositoryHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway

) : PlaylistOperations {

    private val historyDao = appDatabase.historyDao()

    override suspend fun createPlaylist(playlistName: String): Long {
        assertBackgroundThread()

        val added = System.currentTimeMillis()

        val contentValues = ContentValues()
        contentValues.put(Playlists.NAME, playlistName)
        contentValues.put(Playlists.DATE_ADDED, added)
        contentValues.put(Playlists.DATE_MODIFIED, added)
        val uri = context.contentResolver.insert(Playlists.EXTERNAL_CONTENT_URI, contentValues)
        requireNotNull(uri)
        return ContentUris.parseId(uri)
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        assertBackgroundThread()

        val uri = Playlists.Members.getContentUri("external", playlistId)

        val cursor = context.contentResolver.querySql(
            """
            SELECT ${Playlists.Members.PLAY_ORDER}
            FROM $uri
            ORDER BY ${Playlists.Members.PLAY_ORDER} DESC
            LIMIT 1
        """.trimIndent()
        )

        var lastPlayOrder = cursor.use {
            if (it.moveToFirst()) {
                it.getInt(0)
            } else {
                0
            }
        }

        val arrayOf = songIds.map { songId ->
            ContentValues(2).apply {
                put(Playlists.Members.PLAY_ORDER, ++lastPlayOrder)
                put(Playlists.Members.AUDIO_ID, songId)
            }
        }

        context.contentResolver.bulkInsert(uri, arrayOf.toTypedArray())
        context.contentResolver.notifyChange(Playlists.EXTERNAL_CONTENT_URI, null)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val uri = ContentUris.withAppendedId(Playlists.EXTERNAL_CONTENT_URI, playlistId)
        context.contentResolver.delete(uri, null, null)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.TRACK)
                AutoPlaylist.HISTORY.id -> return historyDao.deleteAll()
            }
        }
        val uri = Playlists.Members.getContentUri("external", playlistId)
        context.contentResolver.delete(uri, null, null)
    }

    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {
        assertBackgroundThread()

        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            removeFromAutoPlaylist(playlistId, idInPlaylist)
        } else {
            val uri = Playlists.Members.getContentUri("external", playlistId)
            val trackUri = ContentUris.withAppendedId(uri, idInPlaylist)
            context.contentResolver.delete(trackUri, null, null)
        }
    }

    private suspend fun removeFromAutoPlaylist(playlistId: Long, songId: Long) {
        return when (playlistId) {
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteType.TRACK, songId)
            AutoPlaylist.HISTORY.id -> historyDao.deleteSingle(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        val uri = ContentUris.withAppendedId(Playlists.EXTERNAL_CONTENT_URI, playlistId)
        val values = ContentValues(1).apply {
            put(Playlists.NAME, newTitle)
        }

        context.contentResolver.update(uri, values, null, null)
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return Playlists.Members.moveItem(context.contentResolver, playlistId, from, to)
    }

    override suspend fun removeDuplicated(playlistId: Long) {
        val uri = Playlists.Members.getContentUri("external", playlistId)
        val sql = """
            SELECT ${Playlists.Members._ID}, ${Playlists.Members.AUDIO_ID}
            FROM $uri
            
        """.trimIndent()
        val tracksId = context.contentResolver.querySql(sql).use { cursor ->
            val distinctTrackIds = mutableSetOf<Long>()

            while (cursor.moveToNext()) {
                val trackId = cursor.getLong(Playlists.Members.AUDIO_ID)
                distinctTrackIds.add(trackId)
            }
            distinctTrackIds
        }

        context.contentResolver.delete(uri, null, null)
        addSongsToPlaylist(playlistId, tracksId.toList())
    }

    override suspend fun insertSongToHistory(songId: Long) {
        return historyDao.insert(songId)
    }

}
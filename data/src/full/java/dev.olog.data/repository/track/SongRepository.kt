package dev.olog.data.repository.track

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore.Audio
import android.util.Log
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.TrackQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.io.File
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences
) : BaseRepository<Song, Id>(context), SongGateway {

    private val queries = TrackQueries(
        context.contentResolver, blacklistPrefs,
        sortPrefs, false
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        return context.contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Id): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return context.contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun deleteSingle(id: Id) {
        return deleteInternal(id)
    }

    override suspend fun deleteGroup(ids: List<Song>) {
        for (id in ids) {
            deleteInternal(id.id)
        }
    }

    private fun deleteInternal(id: Id) {
        assertBackgroundThread()
        val path = getByParam(id)!!.path
        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            Log.w("SongRepo", "song not found $id")
            return
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun getByUri(uri: Uri): Song? {
        try {
            val id = getByUriInternal(uri)?.toLong() ?: return null
            return getByParam(id)
        } catch (ex: Exception){
            ex.printStackTrace()
            return null
        }
    }

    @Suppress("DEPRECATION")
    private fun getByUriInternal(uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            when (uri.authority) {
                "com.android.providers.media.documents" -> return DocumentsContract.getDocumentId(uri).split(":")[1]
                "media" -> return uri.lastPathSegment
            }
        }
        var songFile: File? = null
        if (uri.authority == "com.android.externalstorage.documents") {
            val child = uri.path?.split(":", limit = 2) ?: listOf()
            songFile = File(Environment.getExternalStorageDirectory(), child[1])
        }

        if (songFile == null) {
            getFilePathFromUri(uri)?.let { path ->
                songFile = File(path)
            }
        }
        if (songFile == null && uri.path != null) {
            songFile = File(uri.path!!)
        }

        var songId: String? = null

        if (songFile != null) {
            context.contentResolver.query(
                Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(BaseColumns._ID),
                "${Audio.AudioColumns.DATA} = ?",
                arrayOf(songFile!!.absolutePath), null
            )?.use { cursor ->
                cursor.moveToFirst()
                songId = "${cursor.getLong(BaseColumns._ID)}"
            }
        }


        return songId
    }

    @Suppress("DEPRECATION")
    private fun getFilePathFromUri(uri: Uri): String? {
        var path: String? = null
        context.contentResolver.query(
            uri, arrayOf(Audio.Media.DATA),
            null, null, null
        )?.use { cursor ->
            cursor.moveToFirst()
            path = cursor.getString(Audio.Media.DATA)
        }
        return path
    }

    override fun getByAlbumId(albumId: Id): Song? {
        return channel.valueOrNull?.find { it.albumId == albumId }
    }
}
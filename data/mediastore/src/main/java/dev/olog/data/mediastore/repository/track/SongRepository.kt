package dev.olog.data.mediastore.repository.track

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.track.SongGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.mediastore.mapper.toTrack
import dev.olog.data.mediastore.queries.TrackQueries
import dev.olog.data.mediastore.repository.BaseRepository
import dev.olog.data.mediastore.repository.ContentUri
import dev.olog.data.mediastore.utils.*
import dev.olog.shared.android.extensions.toAndroidUri
import dev.olog.shared.value
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.net.URI
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferencesGateway,
    blacklistPrefs: BlacklistPreferences,
    schedulers: Schedulers
) : BaseRepository<Track, Id>(context, schedulers), SongGateway {

    private val queries = TrackQueries(
        schedulers = schedulers,
        contentResolver = contentResolver,
        blacklistPrefs = blacklistPrefs,
        sortPrefs = sortPrefs,
        isPodcast = false
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    override suspend fun queryAll(): List<Track> {
        val cursor = queries.getAll()
        return contentResolver.queryAll(cursor, Cursor::toTrack)
    }

    override suspend fun getByParam(param: Id): Track? {
        val cursor = queries.getByParam(param)
        return contentResolver.queryOne(cursor, Cursor::toTrack)
    }

    override fun observeByParam(param: Id): Flow<Track?> {
        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
    }

    override suspend fun deleteSingle(id: Id) = withContext(NonCancellable) {
        deleteInternal(id)
    }

    override suspend fun deleteGroup(ids: List<Track>) = withContext(NonCancellable) {
        for (id in ids) {
            deleteInternal(id.id)
        }
    }

    private suspend fun deleteInternal(id: Id) {
        val path = getByParam(id)!!.path
        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            Timber.w("track not found $id")
            return
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override suspend fun getByUri(uri: URI): Track? {
        try {
            val id = getByUriInternal(uri.toAndroidUri())
            return getByParam(id)
        } catch (ex: Exception){
            ex.printStackTrace()
            return null
        }
    }

    private fun getByUriInternal(uri: Uri): Long {
        // https://developer.android.com/training/secure-file-sharing/retrieve-info
        // content uri has only two field [_id, _display_name]
        val fileQuery = """
            SELECT ${Audio.Media.DISPLAY_NAME}
            FROM $uri
        """
        val displayName = contentResolver.querySql(fileQuery).use {
            it.moveToFirst()
            it.getString(Audio.Media.DISPLAY_NAME)
        }

        val itemQuery = """
            SELECT ${Audio.Media._ID}
            FROM ${Audio.Media.EXTERNAL_CONTENT_URI}
            WHERE ${Audio.Media.DISPLAY_NAME} = ?
        """
        val id = contentResolver.querySql(itemQuery, arrayOf(displayName)).use {
            it.moveToFirst()
            it.getLong(Audio.Media._ID)
        }
        return id
    }

    override suspend fun getByAlbumId(albumId: Id): Track? {
        return publisher.value?.find { it.albumId == albumId }
            ?: contentResolver.queryOne(queries.getByAlbumId(albumId), Cursor::toTrack)
    }
}
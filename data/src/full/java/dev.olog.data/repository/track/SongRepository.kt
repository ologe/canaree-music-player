package dev.olog.data.repository.track

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.PureUri
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.di.qualifier.Tracks
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.TrackQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.*
import dev.olog.shared.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class SongRepository @Inject constructor(
    @ApplicationContext context: Context,
    schedulers: Schedulers,
    @Tracks private val queries: TrackQueries
) : BaseRepository<Song, Id>(context, schedulers), SongGateway {

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(queries.tableUri, true)
    }

    override fun queryAll(): List<Song> {
//        DON'T ASSERT MAIN THREAD
        val cursor = queries.getAll()
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun getByParam(param: Id): Song? {
        assertBackgroundThread()
        val cursor = queries.getByParam(param)
        return contentResolver.queryOne(cursor) { it.toSong() }
    }

    override fun observeByParam(param: Id): Flow<Song?> {
        val uri = ContentUris.withAppendedId(queries.tableUri, param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getByParam(param) }
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun deleteSingle(id: Id) {
        return deleteInternal(id)
    }

    override suspend fun deleteGroup(ids: List<Id>) {
        for (id in ids) {
            deleteInternal(id)
        }
    }

    private fun deleteInternal(id: Id) {
        assertBackgroundThread()
        val path = getByParam(id)?.path ?: return
        val uri = ContentUris.withAppendedId(queries.tableUri, id)
        val deleted = contentResolver.delete(uri, null, null)
        if (deleted < 1) {
            Timber.w("SongRepo: song not found $id")
            return
        }

        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }

    override fun getByUri(uri: PureUri): Song? {
        val realUri = Uri.fromParts(uri.scheme, uri.ssp, uri.fragment)
        try {
            val id = getByUriInternal(realUri) ?: return null
            return getByParam(id)
        } catch (ex: Exception){
            Timber.e(ex)
            return null
        }
    }

    private fun getByUriInternal(uri: Uri): Long? {
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
            FROM ${queries.tableUri}
            WHERE ${Audio.Media.DISPLAY_NAME} = ?
        """
        val id = contentResolver.querySql(itemQuery, arrayOf(displayName)).use {
            it.moveToFirst()
            it.getLong(Audio.Media._ID)
        }
        return id
    }

    override fun getByAlbumId(albumId: Id): Song? {
        assertBackgroundThread()
        val item = channel.valueOrNull?.find { it.albumId == albumId }
        if (item != null){
            return item
        }

        val cursor = queries.getByAlbumId(albumId)
        return contentResolver.queryOne(cursor) { it.toSong() }
    }
}
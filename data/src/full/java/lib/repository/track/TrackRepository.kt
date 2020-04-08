package dev.olog.lib.repository.track

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio
import dev.olog.contentresolversql.querySql
import dev.olog.domain.entity.PureUri
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.lib.repository.podcast.PodcastRepositoryInternal
import dev.olog.lib.utils.getLong
import dev.olog.lib.utils.getString
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class TrackRepository @Inject constructor(
    context: Context,
    private val songRepository: SongRepositoryInternal,
    private val podcastRepository: PodcastRepositoryInternal

) : TrackGateway {

    private val contentResolver = context.contentResolver
    private val queries = songRepository.queries

    override fun getAllTracks(): List<Song> {
        return songRepository.getAll()
    }

    override fun getAllPodcasts(): List<Song> {
        return podcastRepository.getAll()
    }

    override fun observeAllTracks(): Flow<List<Song>> {
        return songRepository.observeAll()
    }

    override fun observeAllPodcasts(): Flow<List<Song>> {
        return podcastRepository.observeAll()
    }

    override fun getByParam(param: Long): Song? {
        return songRepository.getByParam(param)
    }

    override fun observeByParam(param: Long): Flow<Song?> {
        return songRepository.observeByParam(param)
    }

    override fun getByAlbumId(albumId: Long): Song? {
        return songRepository.getByAlbumId(albumId)
    }

    override suspend fun deleteSingle(id: Long) {
        return deleteInternal(id)
    }

    override suspend fun deleteGroup(ids: List<Long>) {
        for (id in ids) {
            deleteInternal(id)
        }
    }

    private fun deleteInternal(id: Long) {
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
}
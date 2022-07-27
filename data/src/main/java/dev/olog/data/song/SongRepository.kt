package dev.olog.data.song

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.data.utils.getString
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import java.io.File
import javax.inject.Inject

// todo test
internal class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao,
    private val sortRepository: SortRepository,
) : SongGateway {

    override fun getAll(): List<Song> {
        return songDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Song>> {
        return songDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Song? {
        return songDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Song?> {
        return songDao.observeById(id.toString())
            .distinctUntilChanged()
            .mapLatest { it?.toDomain() }
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
        val path = getByParam(id)!!.path
        val uri = ContentUris.withAppendedId(Audio.Media.EXTERNAL_CONTENT_URI, id)
        val deleted = context.contentResolver.delete(uri, null, null)
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
        // https://developer.android.com/training/secure-file-sharing/retrieve-info
        // content uri has only two field [_id, _display_name]
        val fileQuery = """
            SELECT ${Audio.Media.DISPLAY_NAME}
            FROM $uri
        """
        val displayName = context.contentResolver.querySql(fileQuery).use {
            it.moveToFirst()
            it.getString(Audio.Media.DISPLAY_NAME)
        }
        return songDao.getByDisplayName(displayName)?.toDomain()
    }

    override fun getByAlbumId(albumId: Id): Song? {
        return songDao.getByAlbumId(albumId.toString())?.toDomain()
    }

    override fun setSort(sort: AllSongsSort) {
        sortRepository.setAllSongsSort(sort)
    }

    override fun getSort(): AllSongsSort {
        return sortRepository.getAllSongsSort()
    }
}
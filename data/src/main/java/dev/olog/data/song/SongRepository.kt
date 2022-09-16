package dev.olog.data.song

import android.net.Uri
import dev.olog.core.entity.sort.AllSongsSort
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.mediastore.MediaStoreUtils
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

// todo test
class SongRepository @Inject constructor(
    private val songDao: SongDao,
    private val sortRepository: SortRepository,
    private val mediaStoreUtils: MediaStoreUtils,
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

    override suspend fun deleteSingle(id: Long) {
        mediaStoreUtils.delete(id.toString()) {
            getByParam(it.toLong())?.path
        }
    }

    override suspend fun deleteGroup(ids: List<Long>) {
        mediaStoreUtils.deleteGroup(ids.map { it.toString() }) {
            getByParam(it.toLong())?.path
        }
    }

    override fun getByUri(uri: Uri): Song? {
        return mediaStoreUtils.getByUri(uri) {
            songDao.getByDisplayName(it)?.toDomain()
        }
    }

    override fun getByAlbumId(albumId: Long): Song? {
        return songDao.getByAlbumId(albumId.toString())?.toDomain()
    }

    override fun setSort(sort: AllSongsSort) {
        sortRepository.setAllSongsSort(sort)
    }

    override fun getSort(): AllSongsSort {
        return sortRepository.getAllSongsSort()
    }
}
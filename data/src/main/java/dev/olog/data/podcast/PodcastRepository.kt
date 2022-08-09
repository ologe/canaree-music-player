package dev.olog.data.podcast

import android.net.Uri
import dev.olog.core.entity.sort.AllPodcastsSort
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.mediastore.MediaStoreUtils
import dev.olog.data.mediastore.podcast.PodcastPositionDao
import dev.olog.data.mediastore.podcast.PodcastPositionEntity
import dev.olog.data.mediastore.podcast.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    private val podcastDao: PodcastDao,
    private val sortRepository: SortRepository,
    private val mediaStoreUtils: MediaStoreUtils,
    private val podcastPositionDao: PodcastPositionDao,
) : PodcastGateway {

    override fun getAll(): List<Song> {
        return podcastDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Song>> {
        return podcastDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Song? {
        return podcastDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Song?> {
        return podcastDao.observeById(id.toString())
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
            podcastDao.getByDisplayName(it)?.toDomain()
        }
    }

    override fun getByAlbumId(albumId: Long): Song? {
        return podcastDao.getByAlbumId(albumId.toString())?.toDomain()
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        podcastPositionDao.setPosition(PodcastPositionEntity(podcastId, position))
    }

    override fun setSort(sort: AllPodcastsSort) {
        sortRepository.setAllPodcastsSort(sort)
    }

    override fun getSort(): AllPodcastsSort {
        return sortRepository.getAllPodcastsSort()
    }

}
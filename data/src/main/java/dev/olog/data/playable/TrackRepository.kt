package dev.olog.data.playable

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.Sort
import dev.olog.core.sort.TrackSort
import dev.olog.core.track.Song
import dev.olog.core.track.TrackGateway
import dev.olog.data.index.IndexedPlayablesQueries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject

internal class TrackRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val repository: MediaStoreSongRepository,
    private val podcastRepository: MediaStorePodcastEpisodeRepository,
    private val operations: PlayableMediaStoreOperations,
    private val indexedPlayablesQueries: IndexedPlayablesQueries,
) : TrackGateway {

    override fun getAll(type: MediaStoreType): List<Song> {
        return when (type) {
            MediaStoreType.Song -> repository.getAll()
            MediaStoreType.Podcast -> podcastRepository.getAll()
        }
    }

    override fun observeAll(type: MediaStoreType): Flow<List<Song>> {
        return when (type) {
            MediaStoreType.Song -> repository.observeAll()
            MediaStoreType.Podcast -> podcastRepository.observeAll()
        }
    }

    override fun getById(uri: MediaUri): Song? {
        return when (uri.isPodcast) {
            true -> podcastRepository.getById(uri.id)
            false -> repository.getById(uri.id)
        }
    }

    override fun getByCollectionId(collectionUri: MediaUri): List<Song> {
        return when (collectionUri.isPodcast) {
            true -> podcastRepository.getByCollectionId(collectionUri.id)
            false -> repository.getByCollectionId(collectionUri.id)
        }
    }

    override fun observeById(uri: MediaUri): Flow<Song?> {
        return when (uri.isPodcast) {
            true -> podcastRepository.observeById(uri.id)
            false -> repository.observeById(uri.id)
        }
    }

    override fun getByUri(uri: URI): Song? {
        val id = operations.getByUri(uri) ?: return null
        return getById(id)
    }

    override fun getSort(type: MediaStoreType): Sort<TrackSort> {
        return when (type) {
            MediaStoreType.Podcast -> podcastRepository.getSort()
            MediaStoreType.Song -> repository.getSort()
        }
    }

    override fun setSort(type: MediaStoreType, sort: Sort<TrackSort>) {
        return when (type) {
            MediaStoreType.Podcast -> podcastRepository.setSort(sort)
            MediaStoreType.Song -> repository.setSort(sort)
        }
    }

    override suspend fun delete(uris: List<MediaUri>) = withContext(schedulers.io) {
        for (item in uris) {
            val uri = operations.delete(getById(item)) ?: return@withContext
            indexedPlayablesQueries.delete(uri.id)
        }
    }

    override fun getPodcastCurrentPosition(uri: MediaUri, duration: Long): Long {
        return when (uri.isPodcast) {
            true -> podcastRepository.getCurrentPosition(uri.id, duration)
            false -> error("invalid $uri")
        }

    }

    override fun savePodcastCurrentPosition(uri: MediaUri, position: Long) {
        return when (uri.isPodcast) {
            true -> podcastRepository.saveCurrentPosition(uri.id, position)
            false -> error("invalid $uri")
        }
    }
}
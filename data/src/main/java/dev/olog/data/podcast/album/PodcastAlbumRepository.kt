package dev.olog.data.podcast.album

import dev.olog.core.entity.sort.AllPodcastAlbumsSort
import dev.olog.core.entity.sort.PodcastAlbumEpisodesSort
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.data.mediastore.podcast.album.toDomain
import dev.olog.data.mediastore.podcast.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PodcastAlbumRepository @Inject constructor(
    private val albumDao: PodcastAlbumDao,
    private val sortRepository: SortRepository,
) : PodcastAlbumGateway {

    override fun getAll(): List<Album> {
        return albumDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Album>> {
        return albumDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Album? {
        return albumDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Album?> {
        return albumDao.observeById(id.toString())
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return albumDao.getTracksById(id.toString())
            .map { it.toDomain() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return albumDao.observeTracksById(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeLastPlayed(): Flow<List<Album>> {
        return albumDao.observeLastPlayed()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override suspend fun addLastPlayed(id: Long) {
        albumDao.insertLastPlayed(LastPlayedPodcastAlbumEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return albumDao.observeRecentlyAdded()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeSiblings(id: Long): Flow<List<Album>> {
        return albumDao.observeSiblings(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeArtistsAlbums(artistId: Long): Flow<List<Album>> {
        return albumDao.observeArtistAlbums(artistId.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllPodcastAlbumsSort) {
        sortRepository.setAllPodcastAlbumsSort(sort)
    }

    override fun getSort(): AllPodcastAlbumsSort {
        return sortRepository.getAllPodcastAlbumsSort()
    }

    override fun setEpisodeSort(sort: PodcastAlbumEpisodesSort) {
        sortRepository.setPodcastAlbumEpisodesSort(sort)
    }

    override fun getEpisodeSort(): PodcastAlbumEpisodesSort {
        return sortRepository.getPodcastAlbumEpisodesSort()
    }

}
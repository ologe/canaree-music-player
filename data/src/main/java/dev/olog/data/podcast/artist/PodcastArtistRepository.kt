package dev.olog.data.podcast.artist

import dev.olog.core.entity.sort.AllPodcastArtistsSort
import dev.olog.core.entity.sort.PodcastArtistEpisodesSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.data.mediastore.podcast.artist.toDomain
import dev.olog.data.mediastore.podcast.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PodcastArtistRepository @Inject constructor(
    private val artistDao: PodcastArtistDao,
    private val sortRepository: SortRepository,
) : PodcastArtistGateway {

    override fun getAll(): List<Artist> {
        return artistDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Artist>> {
        return artistDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Artist? {
        return artistDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Artist?> {
        return artistDao.observeById(id.toString())
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return artistDao.getTracksById(id.toString()).map { it.toDomain() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return artistDao.observeTracksById(id.toString())
            .mapListItem { it.toDomain() }
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
        return artistDao.observeLastPlayed()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override suspend fun addLastPlayed(id: Long) {
        artistDao.insertLastPlayed(LastPlayedPodcastArtistEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return artistDao.observeRecentlyAdded()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllPodcastArtistsSort) {
        sortRepository.setAllPodcastArtistsSort(sort)
    }

    override fun getSort(): AllPodcastArtistsSort {
        return sortRepository.getAllPodcastArtistsSort()
    }

    override fun setEpisodeSort(sort: PodcastArtistEpisodesSort) {
        sortRepository.setPodcastArtistEpisodesSort(sort)
    }

    override fun getEpisodeSort(): PodcastArtistEpisodesSort {
        return sortRepository.getPodcastArtistEpisodesSort()
    }

}
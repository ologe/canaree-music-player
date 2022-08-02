package dev.olog.data.song.artist

import dev.olog.core.entity.sort.AllArtistsSort
import dev.olog.core.entity.sort.ArtistSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.data.mediastore.song.artist.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// todo test
internal class ArtistRepository @Inject constructor(
    private val artistDao: ArtistDao,
    private val sortRepository: SortRepository,
) : ArtistGateway {

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

    override suspend fun addLastPlayed(id: Id) {
        artistDao.insertLastPlayed(LastPlayedArtistEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return artistDao.observeRecentlyAdded()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllArtistsSort) {
        sortRepository.setAllArtistsSort(sort)
    }

    override fun getSort(): AllArtistsSort {
        return sortRepository.getAllArtistsSort()
    }

    override fun setSongSort(sort: ArtistSongsSort) {
        sortRepository.setArtistSongsSort(sort)
    }

    override fun getSongSort(): ArtistSongsSort {
        return sortRepository.getArtistSongsSort()
    }
}
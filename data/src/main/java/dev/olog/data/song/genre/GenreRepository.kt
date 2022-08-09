package dev.olog.data.song.genre

import dev.olog.core.entity.sort.AllGenresSort
import dev.olog.core.entity.sort.GenreSongsSort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.data.mediastore.song.artist.toDomain
import dev.olog.data.mediastore.song.genre.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    private val genreDao: GenreDao,
    private val sortRepository: SortRepository,
) : GenreGateway {

    override fun getAll(): List<Genre> {
        return genreDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Genre>> {
        return genreDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Genre? {
        return genreDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Genre?> {
        return genreDao.observeById(id.toString())
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return genreDao.getTracksById(id.toString())
            .map { it.toDomain() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return genreDao.observeTracksById(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeSiblings(id: Long): Flow<List<Genre>> {
        return genreDao.observeSiblings(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeMostPlayed(id: Long): Flow<List<Song>> {
        return genreDao.observeMostPlayed(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override suspend fun insertMostPlayed(genreId: Long, songId: Long) {
        genreDao.insertMostPlayed(genreId.toString(), songId.toString())
    }

    override fun observeRelatedArtists(id: Long): Flow<List<Artist>> {
        return genreDao.observeRelatedArtists(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeRecentlyAdded(id: Long): Flow<List<Song>> {
        return genreDao.observeRecentlyAddedSongs(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllGenresSort) {
        sortRepository.setAllGenresSort(sort)
    }

    override fun getSort(): AllGenresSort {
        return sortRepository.getAllGenresSort()
    }

    override fun setSongSort(sort: GenreSongsSort) {
        sortRepository.setGenreSongsSort(sort)
    }

    override fun getSongSort(): GenreSongsSort {
        return sortRepository.getGenreSongsSort()
    }
}
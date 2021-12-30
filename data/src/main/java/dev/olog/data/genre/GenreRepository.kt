package dev.olog.data.genre

import dev.olog.core.author.Artist
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.genre.Genre
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import dev.olog.core.schedulers.Schedulers
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.GenreDetailSort
import dev.olog.core.sort.Sort
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOne
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.sort.SortDao
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    private val schedulers: Schedulers,
    private val queries: GenresQueries,
    private val sortDao: SortDao,
) : GenreGateway {

    override fun getAll(): List<Genre> {
        return queries.selectAllSorted()
            .executeAsList()
            .map(Genres_view::toDomain)
    }

    override fun observeAll(): Flow<List<Genre>> {
        return queries.selectAllSorted()
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_view::toDomain)
    }

    override fun getById(uri: MediaUri): Genre? {
        return queries.selectById(uri.id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeById(uri: MediaUri): Flow<Genre?> {
        return queries.selectById(uri.id)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override fun getTracksById(uri: MediaUri): List<Song> {
        return queries.selectTracksByIdSorted(uri.id)
            .executeAsList()
            .map(Genres_playables_view::toDomain)
    }

    override fun observeTracksById(uri: MediaUri): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_playables_view::toDomain)
    }

    override fun observeMostPlayed(uri: MediaUri): Flow<List<MostPlayedSong>> {
        return queries.selectMostPlayed(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectMostPlayed::toDomain)
    }

    override suspend fun insertMostPlayed(uri: MediaUri, trackUri: MediaUri) {
        queries.incrementMostPlayed(
            genreId = uri.id,
            songId = trackUri.id,
        )
    }

    override fun observeRecentlyAddedTracksById(uri: MediaUri): Flow<List<Song>> {
        return queries.selectRecentlyAddedSongs(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_playables_view::toDomain)
    }

    override fun observeRelatedArtistsById(uri: MediaUri): Flow<List<Artist>> {
        return queries.selectRelatedArtists(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectRelatedArtists::toDomain)
    }

    override fun observeSiblingsById(uri: MediaUri): Flow<List<Genre>> {
        return queries.selectSiblings(uri.id)
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_view::toDomain)
    }

    override fun getSort(): Sort<GenericSort> {
        return sortDao.getGenresSortQuery().executeAsOne()
    }

    override fun setSort(sort: Sort<GenericSort>) {
        sortDao.setGenresSort(sort)
    }

    override fun getDetailSort(): Sort<GenreDetailSort> {
        return sortDao.getDetailGenresSortQuery().executeAsOne()
    }

    override fun observeDetailSort(): Flow<Sort<GenreDetailSort>> {
        return sortDao.getDetailGenresSortQuery()
            .mapToFlowOne(schedulers.io)
    }

    override fun setDetailSort(sort: Sort<GenreDetailSort>) {
        sortDao.setDetailGenresSort(sort)
    }

}
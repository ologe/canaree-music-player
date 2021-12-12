package dev.olog.data.genre

import dev.olog.core.MediaId
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.sort.GenreDetailSort
import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.extension.mapToFlowList
import dev.olog.data.extension.mapToFlowOne
import dev.olog.data.extension.mapToFlowOneOrNull
import dev.olog.data.playable.Songs_view
import dev.olog.data.playable.toDomain
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

    override fun getByParam(param: Id): Genre? {
        return queries.selectById(param)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override fun observeByParam(param: Id): Flow<Genre?> {
        return queries.selectById(param)
            .mapToFlowOneOrNull(schedulers.io)
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return queries.selectTracksByIdSorted(param)
            .executeAsList()
            .map(Genres_playables_view::toDomain)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return queries.selectTracksByIdSorted(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_playables_view::toDomain)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<MostPlayedSong>> {
        return queries.selectMostPlayed(mediaId.categoryId)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectMostPlayed::toDomain)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        val songId = mediaId.leaf!!
        val genreId = mediaId.categoryId
        queries.incrementMostPlayed(songId, genreId)
    }

    override fun observeRecentlyAddedSongs(param: Id): Flow<List<Song>> {
        return queries.selectRecentlyAddedSongs(param)
            .mapToFlowList(schedulers.io)
            .mapListItem(Genres_playables_view::toDomain)
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        return queries.selectRelatedArtists(params)
            .mapToFlowList(schedulers.io)
            .mapListItem(SelectRelatedArtists::toDomain)
    }

    override fun observeSiblings(param: Id): Flow<List<Genre>> {
        return queries.selectSiblings(param)
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
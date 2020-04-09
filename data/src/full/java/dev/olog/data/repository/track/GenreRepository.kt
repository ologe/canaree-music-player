package dev.olog.data.repository.track

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.domain.MediaId
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Genre
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.db.GenreMostPlayedDao
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toGenre
import dev.olog.data.mapper.toSong
import dev.olog.data.model.db.GenreMostPlayedEntity
import dev.olog.data.queries.GenreQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    context: Context,
    contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val trackGateway: TrackGateway,
    private val mostPlayedDao: GenreMostPlayedDao,
    private val schedulers: Schedulers
) : BaseRepository<Genre, Long>(context, schedulers), GenreGateway {

    private val queries = GenreQueries(contentResolver, blacklistPrefs, sortPrefs)

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Genre> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val genres = contentResolver.queryAll(cursor) { it.toGenre() }
        return genres.mapNotNull { genre ->
            // get the size for every genre
            val sizeQueryCursor = queries.countGenreSize(genre.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            if (sizeQuery == 0){
                null
            } else {
                genre.copy(size = sizeQuery)
            }
        }
    }

    override fun getByParam(param: Long): Genre? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Long): Flow<Genre?> {
        return channel.asFlow().map { it.find { it.id == param } }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
            .flowOn(schedulers.cpu)
    }

    override fun observeSiblings(param: Long): Flow<List<Genre>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>> {
        return mostPlayedDao.observeAll(mediaId.categoryId.toLong(), trackGateway)
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId.Track) {
        assertBackgroundThread()
        mostPlayedDao.insert(
            GenreMostPlayedEntity(
                0,
                mediaId.id.toLong(),
                mediaId.categoryId.toLong()
            )
        )
    }

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRelatedArtists(param)) }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun observeRecentlyAdded(param: Long): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) {
            val cursor = queries.getRecentlyAdded(param)
            contentResolver.queryAll(cursor) { it.toSong() }
        }
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        assertBackgroundThread()
        return contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.copy(songs = list.size)
            }
    }
}
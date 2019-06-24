package dev.olog.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.GenreGateway2
import dev.olog.core.gateway.Id
import dev.olog.core.gateway.SongGateway2
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toGenre
import dev.olog.data.queries.GenreQueries
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class GenreRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    appDatabase: AppDatabase,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val songGateway2: SongGateway2
) : BaseRepository<Genre, Id>(context), GenreGateway2 {

    private val queries = GenreQueries(contentResolver, blacklistPrefs, sortPrefs)
    private val mostPlayedDao = appDatabase.genreMostPlayedDao()

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Genre> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val genres = contentResolver.queryAll(cursor) { it.toGenre() }
        return genres.map { genre ->
            // get the size for every playlist
            val sizeQueryCursor = queries.countGenreSize(genre.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            genre.copy(size = sizeQuery)
        }
    }

    override fun getByParam(param: Id): Genre? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Genre?> {
        return channel.asFlow().map { it.find { it.id == param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flow { }
    }

    override fun observeSiblings(id: Id): Flow<List<Genre>> {
        return observeAll()
            .map { it.filter { it.id != id } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return mostPlayedDao.getAll(mediaId.categoryId, songGateway2)
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        assertBackgroundThread()
        songGateway2.getByParam(mediaId.leaf!!)?.let { item ->
            mostPlayedDao.insertOne(
                GenreMostPlayedEntity(
                    0,
                    item.id,
                    mediaId.categoryId
                )
            )
        } ?: Log.w("FolderRepo", "song not found=$mediaId")
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRelatedArtists(params)) }
            .distinctUntilChanged()
            .assertBackground()
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        assertBackgroundThread()
        return context.contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.copy(songs = list.size)
            }
    }
}
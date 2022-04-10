package dev.olog.data.repository.track

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.db.dao.GenreMostPlayedDao
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toGenre
import dev.olog.data.mapper.toPlaylistSong
import dev.olog.data.queries.GenreQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GenreRepository @Inject constructor(
    @ApplicationContext context: Context,
    contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val songGateway2: SongGateway,
    private val mostPlayedDao: GenreMostPlayedDao,
    schedulers: Schedulers
) : BaseRepository<Genre, Id>(context, contentResolver, schedulers), GenreGateway {

    private val queries = GenreQueries(contentResolver, blacklistPrefs, sortPrefs)

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Genre> {
        val cursor = queries.getAll()
        val genres = contentResolver.queryAll(cursor) { it.toGenre() }
        return genres.mapNotNull { genre ->
            // get the size for every genre
            val sizeQueryCursor = queries.countGenreSize(genre.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            if (sizeQuery == 0){
                null
            } else {
                genre.withSongs(sizeQuery)
            }
        }
    }

    override fun getByParam(param: Id): Genre? {
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Genre?> {
        return channel.asFlow().map { it.find { it.id == param } }
            .distinctUntilChanged()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toPlaylistSong() }
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override fun observeSiblings(param: Id): Flow<List<Genre>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return mostPlayedDao.getAll(mediaId.categoryId, songGateway2)
            .distinctUntilChanged()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        mostPlayedDao.insertOne(
            GenreMostPlayedEntity(
                0,
                mediaId.leaf!!,
                mediaId.categoryId
            )
        )
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRelatedArtists(params)) }
            .distinctUntilChanged()
    }

    override fun observeRecentlyAdded(path: Id): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) {
            val cursor = queries.getRecentlyAdded(path)
            contentResolver.queryAll(cursor) { it.toPlaylistSong() }
        }
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        return contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.withSongs(list.size)
            }
    }
}
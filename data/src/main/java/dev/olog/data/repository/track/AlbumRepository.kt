package dev.olog.data.repository.track

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.HasLastPlayed
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.local.recently.played.RecentlyPlayedAlbumDao
import dev.olog.data.mapper.toAlbum
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.AlbumsQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.value
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val lastPlayedDao: RecentlyPlayedAlbumDao,
    schedulers: Schedulers
) : BaseRepository<Album, Id>(context, schedulers), AlbumGateway {

    private val queries = AlbumsQueries(
        schedulers = schedulers,
        contentResolver = contentResolver,
        blacklistPrefs = blacklistPrefs,
        sortPrefs = sortPrefs,
        isPodcast = false
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private suspend fun extractAlbums(cursor: Cursor): List<Album> {
        return contentResolver.queryAll(cursor, Cursor::toAlbum)
            .groupBy { it.id }
            .map { (_, list) ->
                val album = list[0]
                album.withSongs(list.size)
            }
    }

    override suspend fun queryAll(): List<Album> {
        val cursor = queries.getAll()
        return extractAlbums(cursor)
    }

    override suspend fun getByParam(param: Id): Album? {
        return publisher.value?.find { it.id == param }
            ?: contentResolver.queryOne(queries.getByParam(param), Cursor::toAlbum)
    }

    override fun observeByParam(param: Id): Flow<Album?> {
        return observeAll()
            .map { list -> list.find { it.id == param } }
            .distinctUntilChanged()

    }

    override suspend fun getTrackListByParam(param: Id): List<Song> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor, Cursor::toSong)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override fun observeLastPlayed(): Flow<List<Album>> {
        return observeAll().combine(lastPlayedDao.observeAll()) { all, lastPlayed ->
            if (all.size < HasLastPlayed.MIN_ITEMS) {
                listOf() // too few album to show recents
            } else {
                lastPlayed.asSequence()
                    .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                    .take(HasLastPlayed.MAX_ITEM_TO_SHOW)
                    .toList()
            }
        }.distinctUntilChanged()
    }

    override suspend fun addLastPlayed(id: Id) {
        lastPlayedDao.insertOne(id)
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return flowOf(emptyList())
        // TODO crash on android 11, see BaseQueries
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractAlbums(queries.getRecentlyAdded()) }
            .distinctUntilChanged()
    }

    override fun observeSiblings(param: Id): Flow<List<Album>> {
        return observeAll()
            .map {
                val artistId = it.find { it.id == param }?.artistId ?: -1
                it.asSequence()
                    .filter { it.artistId == artistId }
                    .filter { it.id != param }
                    .toList()
            }
            .distinctUntilChanged()
    }

    override fun observeArtistsAlbums(artistId: Id): Flow<List<Album>> {
        return observeAll()
            .map { it.filter { it.artistId == artistId } }
            .distinctUntilChanged()
    }
}
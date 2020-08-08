package dev.olog.data.repository.track

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.base.HasLastPlayed
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferences
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.db.LastPlayedAlbumDao
import dev.olog.data.mapper.toAlbum
import dev.olog.data.mapper.toSong
import dev.olog.data.model.db.LastPlayedAlbumEntity
import dev.olog.data.queries.AlbumsQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.shared.android.utils.assertBackgroundThread
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val lastPlayedDao: LastPlayedAlbumDao,
    private val schedulers: Schedulers
) : BaseRepository<Album, Long>(context, schedulers), AlbumGateway {

    private val queries = AlbumsQueries(contentResolver, blacklistPrefs, sortPrefs)

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractAlbums(cursor: Cursor): List<Album> {
        assertBackgroundThread()
        return contentResolver.queryAll(cursor) { it.toAlbum() }
            .groupBy { it.id }
            .map { (_, list) ->
                val album = list[0]
                album.copy(songs = list.size)
            }
    }

    override fun queryAll(): List<Album> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        return extractAlbums(cursor)
    }

    override fun getByParam(param: Long): Album? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Long): Flow<Album?> {
        return channel.asFlow().map { list -> list.find { it.id == param } }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        assertBackgroundThread()
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
            .flowOn(schedulers.cpu)
    }

    override fun observeLastPlayed(): Flow<List<Album>> {
        return observeAll().combine(lastPlayedDao.getAll()) { all, lastPlayed ->
            if (all.size < HasLastPlayed.MIN_ITEMS) {
                listOf() // too few album to show recents
            } else {
                lastPlayed.asSequence()
                    .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                    .take(HasLastPlayed.MAX_ITEM_TO_SHOW)
                    .toList()
            }
        }.distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override suspend fun addLastPlayed(id: Long) {
        assertBackgroundThread()
        lastPlayedDao.insert(LastPlayedAlbumEntity(id = id))
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractAlbums(queries.getRecentlyAdded()) }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun observeSiblings(param: Long): Flow<List<Album>> {
        return observeAll()
            .map {
                val artistId = it.find { it.id == param }?.artistId ?: -1
                it.asSequence()
                    .filter { it.artistId == artistId }
                    .filter { it.id != param }
                    .toList()
            }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override fun observeArtistsAlbums(artistId: Long): Flow<List<Album>> {
        return observeAll()
            .map { it.filter { it.artistId == artistId } }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }
}
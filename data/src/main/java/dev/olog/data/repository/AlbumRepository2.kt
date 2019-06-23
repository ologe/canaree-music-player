package dev.olog.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.core.gateway.Id
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mapper.toAlbum
import dev.olog.data.queries.AlbumsQueries
import dev.olog.data.utils.queryAll
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class AlbumRepository2 @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    appDatabase: AppDatabase
) : BaseRepository<Album, Id>(context), AlbumGateway2 {

    private val queries = AlbumsQueries(contentResolver, blacklistPrefs, sortPrefs, false)
    private val lastPlayedDao = appDatabase.lastPlayedAlbumDao()

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractAlbums(cursor: Cursor): List<Album> {
        assertBackgroundThread()
        return context.contentResolver.queryAll(cursor) { it.toAlbum() }
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

    override fun getByParam(param: Id): Album? {
        assertBackgroundThread()
        return channel.valueOrNull?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Album?> {
        return channel.asFlow().map { list -> list.find { it.id == param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        assertBackgroundThread()
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        assertBackgroundThread()
        return flow { }
    }

    override fun observeLastPlayed(): Flow<List<Album>> {
        return observeAll().combineLatest(lastPlayedDao.getAll().asFlow()) { all, lastPlayed ->
            if (all.size < 5) {
                listOf() // too few album to show recents
            } else {
                lastPlayed.asSequence()
                    .mapNotNull { last -> all.firstOrNull { it.id == last.id } }
                    .take(5)
                    .toList()
            }
        }.assertBackground()

    }

    override suspend fun addLastPlayed(id: Id) {
        assertBackgroundThread()
        lastPlayedDao.insertOne(id)
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        val cursor = queries.getRecentlyAdded()
        return observeByParamInternal(ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)) {
            extractAlbums(cursor)
        }.distinctUntilChanged()
            .assertBackground()
    }
}
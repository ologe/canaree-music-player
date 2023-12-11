package dev.olog.data.repository.track

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.HasLastPlayed
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.schedulers.Schedulers
import dev.olog.data.db.dao.LastPlayedArtistDao
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.ArtistQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ArtistRepository @Inject constructor(
    @ApplicationContext context: Context,
    contentResolver: ContentResolver,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    private val lastPlayedDao: LastPlayedArtistDao,
    schedulers: Schedulers
) : BaseRepository<Artist, Id>(context, contentResolver, schedulers), ArtistGateway {

    private val queries = ArtistQueries(contentResolver, blacklistPrefs, sortPrefs, false)

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private fun extractArtists(cursor: Cursor): List<Artist> {
        return contentResolver.queryAll(cursor) { it.toArtist() }
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.withSongs(list.size)
            }
    }

    override fun queryAll(): List<Artist> {
        val cursor = queries.getAll()
        return extractArtists(cursor)
    }

    override fun getByParam(param: Id): Artist? {
        return channel.replayCache.firstOrNull()?.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Artist?> {
        return channel.map { list -> list.find { it.id == param } }
            .distinctUntilChanged()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toSong() }
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
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
    }

    override suspend fun addLastPlayed(id: Id) {
        lastPlayedDao.insertOne(id)
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return flowOf(emptyList())
        // TODO crash on android 11, see BaseQueries
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRecentlyAdded()) }
            .distinctUntilChanged()
    }
}
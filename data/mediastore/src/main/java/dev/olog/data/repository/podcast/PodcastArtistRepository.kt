package dev.olog.data.repository.podcast

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.base.HasLastPlayed
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.local.recently.played.RecentlyPlayedPodcastArtistDao
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toSong
import dev.olog.data.queries.ArtistQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryOne
import dev.olog.shared.value
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PodcastArtistRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferencesGateway,
    blacklistPrefs: BlacklistPreferences,
    private val lastPlayedDao: RecentlyPlayedPodcastArtistDao,
    schedulers: Schedulers
) : BaseRepository<Artist, Id>(context, schedulers), PodcastArtistGateway {

    private val queries = ArtistQueries(
        schedulers = schedulers,
        contentResolver = contentResolver,
        blacklistPrefs = blacklistPrefs,
        sortPrefs = sortPrefs,
        isPodcast = true
    )

    init {
        firstQuery()
    }

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
    }

    private suspend fun extractArtists(cursor: Cursor): List<Artist> {
        return contentResolver.queryAll(cursor, Cursor::toArtist)
            .groupBy { it.id }
            .map { (_, list) ->
                val artist = list[0]
                artist.withSongs(songs = list.size)
            }
    }

    override suspend fun queryAll(): List<Artist> {
        val cursor = queries.getAll()
        return extractArtists(cursor)
    }

    override suspend fun getByParam(param: Id): Artist? {
        return publisher.value?.find { it.id == param }
            ?: contentResolver.queryOne(queries.getByParam(param), Cursor::toArtist)
    }

    override fun observeByParam(param: Id): Flow<Artist?> {
        return observeAll()
            .map { list -> list.find { it.id == param } }
            .distinctUntilChanged()
    }

    override suspend fun getTrackListByParam(param: Id): List<Track> {
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor, Cursor::toSong)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Track>> {
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
    }

    override fun observeLastPlayed(): Flow<List<Artist>> {
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

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return flowOf(emptyList())
        // TODO crash on android 11, see BaseQueries
        val contentUri = ContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true)
        return observeByParamInternal(contentUri) { extractArtists(queries.getRecentlyAdded()) }
            .distinctUntilChanged()
    }

    override fun observeSiblings(param: Id): Flow<List<Artist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
    }
}
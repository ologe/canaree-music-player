package dev.olog.data.repository.track

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.prefs.BlacklistPreferences
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.R
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PlaylistMostPlayedEntity
import dev.olog.data.mapper.toArtist
import dev.olog.data.mapper.toPlaylist
import dev.olog.data.mapper.toPlaylistSong
import dev.olog.data.queries.PlaylistQueries
import dev.olog.data.repository.BaseRepository
import dev.olog.data.repository.ContentUri
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.data.utils.queryAll
import dev.olog.data.utils.queryCountRow
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext context: Context,
    sortPrefs: SortPreferences,
    blacklistPrefs: BlacklistPreferences,
    appDatabase: AppDatabase,
    private val songGateway: SongGateway,
    private val helper: PlaylistRepositoryHelper,
    private val favoriteGateway: FavoriteGateway
) : BaseRepository<Playlist, Id>(context),
    PlaylistGateway, PlaylistOperations by helper {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)
    private val queries = PlaylistQueries(contentResolver, blacklistPrefs, sortPrefs)
    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    override fun registerMainContentUri(): ContentUri {
        return ContentUri(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, true)
    }

    override fun queryAll(): List<Playlist> {
        assertBackgroundThread()
        val cursor = queries.getAll()
        val playlists = contentResolver.queryAll(cursor) { it.toPlaylist() }
        return playlists.map { playlist ->
            // get the size for every playlist
            val sizeQueryCursor = queries.countPlaylistSize(playlist.id)
            val sizeQuery = contentResolver.queryCountRow(sizeQueryCursor)
            playlist.withSongs(sizeQuery)
        }
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
        val all = if (AutoPlaylist.isAutoPlaylist(param)){
            getAllAutoPlaylists()
        } else {
            channel.value
        }
        return all.find { it.id == param }
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return flow { emit(getByParam(param)) }
        }

        return channel.asFlow()
            .map { it.find { it.id == param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        assertBackgroundThread()
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        val cursor = queries.getSongList(param)
        return contentResolver.queryAll(cursor) { it.toPlaylistSong() }
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
                .assertBackground()
        }

        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", param)
        val contentUri = ContentUri(uri, true)
        return observeByParamInternal(contentUri) { getTrackListByParam(param) }
            .assertBackground()
    }

    private fun getAutoPlaylistsTracks(param: Id): List<Song> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> songGateway.getAll().sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getTracks()
            AutoPlaylist.HISTORY.id -> historyDao.getTracks(songGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Id): Flow<List<Song>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> songGateway.observeAll().map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observeTracks()
            AutoPlaylist.HISTORY.id -> historyDao.observeTracks(songGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        assertBackgroundThread()
        return listOf(
            createAutoPlaylist(AutoPlaylist.LAST_ADDED.id, autoPlaylistTitles[0]),
            createAutoPlaylist(AutoPlaylist.FAVORITE.id, autoPlaylistTitles[1]),
            createAutoPlaylist(AutoPlaylist.HISTORY.id, autoPlaylistTitles[2])
        )
    }

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0, false)
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        val folderPath = mediaId.categoryId
        return mostPlayedDao.getAll(folderPath, songGateway)
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        assertBackgroundThread()
        mostPlayedDao.insertOne(
            PlaylistMostPlayedEntity(
                0,
                mediaId.leaf!!,
                mediaId.categoryId
            )
        )
    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .assertBackground()
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
                artist.withSongs(list.size)
            }
    }
}
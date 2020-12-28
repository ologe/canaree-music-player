package dev.olog.data.repository.track

import android.content.Context
import android.provider.MediaStore.Audio.Playlists.*
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.R
import dev.olog.data.local.history.HistoryDao
import dev.olog.data.local.most.played.PlaylistMostPlayedDao
import dev.olog.data.local.most.played.PlaylistMostPlayedEntity
import dev.olog.data.local.playlist.PlaylistDao
import dev.olog.data.local.playlist.PlaylistEntity
import dev.olog.data.local.playlist.PlaylistTrackEntity
import dev.olog.data.local.playlist.toDomain
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.shared.mapListItem
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    private val artistGateway: ArtistGateway,
    private val helper: PlaylistRepositoryHelper,
    private val favoriteGateway: FavoriteGateway,
    private val historyDao: HistoryDao,
    private val mostPlayedDao: PlaylistMostPlayedDao,
    private val playlistDao: PlaylistDao
) : PlaylistGateway, PlaylistOperations by helper {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)

    override suspend fun getAll(): List<Playlist> {
        populatePlaylistTables()
        val result = playlistDao.getAllPlaylists()
        return result.map(PlaylistEntity::toDomain)
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return playlistDao.observeAllPlaylists()
            .onStart { populatePlaylistTables() }
            .distinctUntilChanged()
            .mapListItem(PlaylistEntity::toDomain)
    }

    override suspend fun getByParam(param: Id): Playlist? {
        return if (AutoPlaylist.isAutoPlaylist(param)){
            getAllAutoPlaylists().find { it.id == param }
        } else {
            playlistDao.getPlaylistById(param)?.toDomain()
        }
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return flow { emit(getByParam(param)) }
        }

        return playlistDao.observePlaylistById(param)
            .map { it?.toDomain() }
    }

    override suspend fun getTrackListByParam(param: Id): List<Track> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        // TODO sort
        return playlistDao.getPlaylistTracks(param, songGateway)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Track>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
        }
        // TODO sort
        return playlistDao.observePlaylistTracks(param, songGateway)
    }

    private suspend fun getAutoPlaylistsTracks(param: Id): List<Track> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> songGateway.getAll()
                .sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getTracks()
            AutoPlaylist.HISTORY.id -> historyDao.getTracks(songGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Id): Flow<List<Track>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> songGateway.observeAll().map { list ->
                list.sortedByDescending { it.dateAdded }
            }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observeTracks()
            AutoPlaylist.HISTORY.id -> historyDao.observeTracks(songGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(AutoPlaylist.LAST_ADDED.id, autoPlaylistTitles[0]),
            createAutoPlaylist(AutoPlaylist.FAVORITE.id, autoPlaylistTitles[1]),
            createAutoPlaylist(AutoPlaylist.HISTORY.id, autoPlaylistTitles[2])
        )
    }

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(
            id = id,
            title = title,
            size = 0,
            isPodcast = false
        )
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Track>> {
        val folderPath = mediaId.categoryId
        return mostPlayedDao.observeAll(folderPath, songGateway)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {
        mostPlayedDao.insertOne(
            PlaylistMostPlayedEntity(
                songId = mediaId.leaf!!,
                playlistId = mediaId.categoryId
            )
        )
    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        return observeTrackListByParam(params)
            .map {  songList ->
                val artists = songList.groupBy { it.artistId }
                    .map { it.key }
                artistGateway.getAll()
                    .filter { artists.contains(it.id) }
            }
    }

    private suspend fun populatePlaylistTables() = withContext(NonCancellable) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "populated_with_legacy_playlist"
        val populated = prefs.getBoolean(key, false)
        if (populated){
            return@withContext
        }
        if (playlistDao.getAllPlaylists().isNotEmpty()){
            return@withContext
        }

        val savedPlaylistSql = """
                SELECT $_ID, $NAME
                FROM $EXTERNAL_CONTENT_URI
                ORDER BY $DEFAULT_SORT_ORDER
            """
        context.contentResolver.querySql(savedPlaylistSql).use {
            while (it.moveToNext()) {
                try {
                    val playlistId = it.getLong(0)
                    val playlistName = it.getString(1) ?: continue

                    val playlist = PlaylistEntity(playlistId, playlistName, 0)
                    playlistDao.createPlaylist(playlist)
                    populatePlaylistWithTracks(playlistId)
                } catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }

        prefs.edit {
            putBoolean(key, true)
        }
    }

    private suspend fun populatePlaylistWithTracks(playlistId: Long) = withContext(NonCancellable) {
        val savedPlaylistSql = """
                SELECT ${Members._ID}, ${Members.AUDIO_ID}
                FROM ${Members.getContentUri("external", playlistId)}
                ORDER BY ${Members.DEFAULT_SORT_ORDER}
            """
        val tracks = mutableListOf<PlaylistTrackEntity>()
        context.contentResolver.querySql(savedPlaylistSql).use {
            while (it.moveToNext()) {
                val idInPlaylist = it.getLong(0)
                val trackId = it.getLong(1)
                val playlistTrack = PlaylistTrackEntity(0, idInPlaylist, trackId, playlistId)
                tracks.add(playlistTrack)
            }
        }
        playlistDao.insertTracks(tracks)
    }

}
package dev.olog.data.repository.track

import android.content.Context
import android.provider.MediaStore.Audio.Playlists.*
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.R
import dev.olog.data.db.dao.HistoryDao
import dev.olog.data.db.dao.PlaylistDao
import dev.olog.data.db.dao.PlaylistMostPlayedDao
import dev.olog.data.db.entities.PlaylistEntity
import dev.olog.data.db.entities.PlaylistMostPlayedEntity
import dev.olog.data.db.entities.PlaylistTrackEntity
import dev.olog.data.mapper.toDomain
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.shared.mapListItem
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.flow.*
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

    override fun getAll(): List<Playlist> {
        populatePlaylistTables()
        assertBackgroundThread()
        val result = playlistDao.getAllPlaylists()
        return result.map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return playlistDao.observeAllPlaylists()
            .onStart { populatePlaylistTables() }
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
            .assertBackground()
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
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
            .map { it }
            .distinctUntilChanged()
            .map { it?.toDomain() }
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        assertBackgroundThread()
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        // TODO sort
        return playlistDao.getPlaylistTracks(param, songGateway)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
                .assertBackground()
        }
        // TODO sort
        return playlistDao.observePlaylistTracks(param, songGateway)
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
        return observeTrackListByParam(params)
            .map {  songList ->
                val artists = songList.groupBy { it.artistId }
                    .map { it.key }
                artistGateway.getAll()
                    .filter { artists.contains(it.id) }
            }
    }

    private fun populatePlaylistTables() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val key = "populated_with_legacy_playlist"
        val populated = prefs.getBoolean(key, false)
        if (populated){
            return
        }
        if (playlistDao.getAllPlaylists().isNotEmpty()){
            return
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

    private fun populatePlaylistWithTracks(playlistId: Long) {
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
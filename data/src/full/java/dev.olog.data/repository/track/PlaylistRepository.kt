package dev.olog.data.repository.track

import android.content.Context
import android.provider.MediaStore.Audio.Playlists.*
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dev.olog.contentresolversql.querySql
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.R
import dev.olog.data.db.HistoryDao
import dev.olog.data.db.PlaylistDao
import dev.olog.data.db.PlaylistMostPlayedDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.model.db.PlaylistEntity
import dev.olog.data.model.db.PlaylistMostPlayedEntity
import dev.olog.data.model.db.PlaylistTrackEntity
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.shared.ApplicationContext
import dev.olog.shared.mapListItem
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.text.Collator
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackGateway: TrackGateway,
    private val artistGateway: ArtistGateway,
    private val helper: PlaylistRepositoryHelper,
    private val favoriteGateway: FavoriteGateway,
    private val historyDao: HistoryDao,
    private val mostPlayedDao: PlaylistMostPlayedDao,
    private val playlistDao: PlaylistDao,
    private val sortPreferences: SortPreferences
) : PlaylistGateway, PlaylistOperations by helper {

    private val collator by lazy {
        Collator.getInstance().apply {
            strength = Collator.NO_DECOMPOSITION
        }
    }

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

    override fun getByParam(param: Long): Playlist? {
        assertBackgroundThread()
        return if (AutoPlaylist.isAutoPlaylist(param)){
            getAllAutoPlaylists().find { it.id == param }
        } else {
            playlistDao.getPlaylistById(param)?.toDomain()
        }
    }

    override fun observeByParam(param: Long): Flow<Playlist?> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return flow { emit(getByParam(param)) }
        }

        return playlistDao.observePlaylistById(param)
            .map { it }
            .distinctUntilChanged()
            .map { it?.toDomain() }
            .assertBackground()
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        assertBackgroundThread()
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        return playlistDao.getPlaylistTracks(param, trackGateway)
            .sortedWith(trackListComparator(sortPreferences.getDetailPlaylistSort()))
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
                .assertBackground()
        }
        return playlistDao.observePlaylistTracks(param, trackGateway)
            .map { it.sortedWith(trackListComparator(sortPreferences.getDetailPlaylistSort())) }
    }

    private fun getAutoPlaylistsTracks(param: Long): List<Song> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> trackGateway.getAllTracks().sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getTracks()
            AutoPlaylist.HISTORY.id -> historyDao.getTracks(trackGateway)
            else -> throwNotHandled("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Long): Flow<List<Song>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> trackGateway.observeAllTracks().map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observeTracks()
            AutoPlaylist.HISTORY.id -> historyDao.observeTracks(trackGateway)
            else -> throwNotHandled("invalid auto playlist id")
        }
    }

    private fun trackListComparator(sort: SortEntity): Comparator<Song> {
        val asc = sort.arranging == SortArranging.ASCENDING
        return Comparator { o1, o2 ->
            when (sort.type) {
                SortType.CUSTOM -> 0 // keep current sort
                SortType.TITLE -> if (asc) collator.compare(o1.title, o2.title) else collator.compare(o2.title, o1.title)
                SortType.ARTIST -> if (asc) collator.compare(o1.artist, o2.artist) else collator.compare(o2.artist, o1.artist)
                SortType.ALBUM -> if (asc) collator.compare(o1.album, o2.album) else collator.compare(o2.album, o1.album)
                SortType.ALBUM_ARTIST -> if (asc) collator.compare(o1.albumArtist, o2.albumArtist) else collator.compare(o2.albumArtist, o1.albumArtist)
                SortType.DURATION -> (if (asc) o1.duration - o2.duration else o2.duration - o1.duration).toInt()
                SortType.RECENTLY_ADDED -> (if (asc) o2.dateAdded - o1.dateAdded else o1.dateAdded - o2.dateAdded).toInt()
                SortType.TRACK_NUMBER -> {
                    // compare by disc number
                    var res =  if (asc) o1.discNumber - o2.discNumber else o2.discNumber - o1.discNumber
                    if (res == 0) {
                        // compare by track number
                        res = (if (asc) o1.trackNumber - o2.trackNumber else o2.trackNumber - o1.trackNumber).toInt()
                        if (res == 0) {
                            // compare by title
                            if (asc) collator.compare(o1.title, o2.title) else collator.compare(o2.title, o1.title)
                        }
                    }
                    res
                }
            }
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

    override fun observeMostPlayed(mediaId: MediaId.Category): Flow<List<Song>> {
        val folderPath = mediaId.categoryId
        return mostPlayedDao.getAll(folderPath, trackGateway)
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun insertMostPlayed(mediaId: MediaId.Track) {
        assertBackgroundThread()
        mostPlayedDao.insert(
            PlaylistMostPlayedEntity(
                0,
                mediaId.id,
                mediaId.categoryId
            )
        )
    }

    override fun observeSiblings(param: Long): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        return observeTrackListByParam(param)
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

                    val playlist = PlaylistEntity(
                        playlistId,
                        playlistName,
                        0
                    )
                    playlistDao.createPlaylist(playlist)
                    populatePlaylistWithTracks(playlistId)
                } catch (ex: Exception){
                    Timber.e(ex)
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
                val playlistTrack = PlaylistTrackEntity(
                    0,
                    idInPlaylist,
                    trackId,
                    playlistId
                )
                tracks.add(playlistTrack)
            }
        }
        playlistDao.insertTracks(tracks)
    }

}
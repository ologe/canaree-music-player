package dev.olog.data.repository.podcast

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.R
import dev.olog.data.db.dao.HistoryDao
import dev.olog.data.db.dao.PodcastPlaylistDao
import dev.olog.data.db.entities.PodcastPlaylistEntity
import dev.olog.data.db.entities.PodcastPlaylistTrackEntity
import dev.olog.data.mapper.toDomain
import dev.olog.shared.mapListItem
import dev.olog.shared.swap
import dev.olog.shared.assertBackground
import dev.olog.shared.assertBackgroundThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val podcastGateway: PodcastGateway,
    private val favoriteGateway: FavoriteGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val historyDao: HistoryDao,
    private val podcastPlaylistDao: PodcastPlaylistDao
) : PodcastPlaylistGateway {

    private val autoPlaylistTitles = context.resources.getStringArray(R.array.common_auto_playlists)

    override fun getAll(): List<Playlist> {
        assertBackgroundThread()
        val result = podcastPlaylistDao.getAllPlaylists()
        return result.map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return podcastPlaylistDao.observeAllPlaylists()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
            .assertBackground()
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(AutoPlaylist.LAST_ADDED.id, autoPlaylistTitles[0]),
            createAutoPlaylist(AutoPlaylist.FAVORITE.id, autoPlaylistTitles[1]),
            createAutoPlaylist(AutoPlaylist.HISTORY.id, autoPlaylistTitles[2])
        )
    }

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0, true)
    }

    override fun getByParam(param: Long): Playlist? {
        assertBackgroundThread()
        return if (AutoPlaylist.isAutoPlaylist(param)){
            getAllAutoPlaylists().find { it.id == param }
        } else {
            podcastPlaylistDao.getPlaylistById(param)?.toDomain()
        }
    }

    override fun observeByParam(param: Long): Flow<Playlist?> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return flow { emit(getByParam(param)) }
        }

        return podcastPlaylistDao.observePlaylistById(param)
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
        return podcastPlaylistDao.getPlaylistTracks(param, podcastGateway)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
                .assertBackground()
        }
        return podcastPlaylistDao.observePlaylistTracks(param, podcastGateway)
    }

    private fun getAutoPlaylistsTracks(param: Long): List<Song> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> podcastGateway.getAll().sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getPodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.getPodcasts(podcastGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Long): Flow<List<Song>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> podcastGateway.observeAll().map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observePodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.observePodcasts(podcastGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    override fun observeSiblings(param: Long): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        assertBackgroundThread()
        return podcastPlaylistDao.createPlaylist(PodcastPlaylistEntity(name = playlistName, size = 0))
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        return podcastPlaylistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        return podcastPlaylistDao.deletePlaylist(playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        require(AutoPlaylist.isAutoPlaylist(playlistId))
        when (playlistId) {
            AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
            AutoPlaylist.HISTORY.id -> return historyDao.deleteAllPodcasts()
        }
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        assertBackgroundThread()

        var maxIdInPlaylist = (podcastPlaylistDao.getPlaylistMaxId(playlistId) ?: 1).toLong()
        val tracks = songIds.map {
            PodcastPlaylistTrackEntity(
                playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                podcastId = it
            )
        }
        podcastPlaylistDao.insertTracks(tracks)
    }

    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist)
    }

    private suspend fun removeFromAutoPlaylist(playlistId: Long, songId: Long) {
        return when (playlistId) {
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteType.PODCAST, songId)
            AutoPlaylist.HISTORY.id -> historyDao.deleteSinglePodcast(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override suspend fun removeDuplicated(playlistId: Long) {
        val notDuplicate = podcastPlaylistDao.getPlaylistTracksImpl(playlistId)
            .asSequence()
            .groupBy { it.podcastId }
            .map { it.value[0] }
            .toList()
        podcastPlaylistDao.deletePlaylistTracks(playlistId)
        podcastPlaylistDao.insertTracks(notDuplicate)
    }

    override suspend fun insertPodcastToHistory(podcastId: Long) {
        return historyDao.insertPodcasts(podcastId)
    }

    override fun observeRelatedArtists(params: Long): Flow<List<Artist>> {
        return observeTrackListByParam(params)
            .map {  songList ->
                val artists = songList.groupBy { it.artistId }
                    .map { it.key }
                podcastArtistGateway.getAll()
                    .filter { artists.contains(it.id) }
            }
    }

    override suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>) =
        kotlinx.coroutines.withContext(Dispatchers.IO) {
            var trackList = podcastPlaylistDao.getPlaylistTracksImpl(playlistId)
            for ((from, to) in moveList) {
                trackList.swap(from, to)
            }
            trackList = trackList.mapIndexed { index, entity -> entity.copy(idInPlaylist = index.toLong()) }
            podcastPlaylistDao.updateTrackList(trackList)
        }

}
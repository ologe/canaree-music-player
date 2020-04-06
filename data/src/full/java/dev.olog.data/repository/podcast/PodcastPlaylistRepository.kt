package dev.olog.data.repository.podcast

import android.content.Context
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.Playlist
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.data.R
import dev.olog.data.db.HistoryDao
import dev.olog.data.db.PodcastPlaylistDao
import dev.olog.data.mapper.toDomain
import dev.olog.data.model.db.PodcastPlaylistEntity
import dev.olog.data.model.db.PodcastPlaylistTrackEntity
import dev.olog.core.ApplicationContext
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.core.mapListItem
import dev.olog.core.swap
import dev.olog.shared.throwNotHandled
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
    @dev.olog.core.ApplicationContext private val context: Context,
    private val trackGateway: TrackGateway,
    private val favoriteGateway: FavoriteGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway,
    private val historyDao: HistoryDao,
    private val podcastPlaylistDao: PodcastPlaylistDao,
    private val schedulers: Schedulers
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
            .flowOn(schedulers.cpu)
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
            .flowOn(schedulers.cpu)
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        assertBackgroundThread()
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        return podcastPlaylistDao.getPlaylistTracks(param, trackGateway)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
                .flowOn(schedulers.cpu)
        }
        return podcastPlaylistDao.observePlaylistTracks(param, trackGateway)
            .flowOn(schedulers.cpu)
    }

    private fun getAutoPlaylistsTracks(param: Long): List<Song> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> trackGateway.getAllPodcasts()
                .sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getPodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.getPodcasts(trackGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Long): Flow<List<Song>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> trackGateway.observeAllPodcasts()
                .map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observePodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.observePodcasts(trackGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    override fun observeSiblings(param: Long): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .flowOn(schedulers.cpu)
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        assertBackgroundThread()
        return podcastPlaylistDao.createPlaylist(
            PodcastPlaylistEntity(
                name = playlistName,
                size = 0
            )
        )
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        return podcastPlaylistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        return podcastPlaylistDao.deletePlaylist(playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        require(AutoPlaylist.isAutoPlaylist(playlistId))
        return when (playlistId) {
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteAll(FavoriteTrackType.PODCAST)
            AutoPlaylist.HISTORY.id -> historyDao.deleteAllPodcasts()
            AutoPlaylist.LAST_ADDED.id -> {}
            else -> throwNotHandled("not an autoplaylist $playlistId")
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
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteTrackType.PODCAST, songId)
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

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        return observeTrackListByParam(param)
            .map {  songList ->
                val artists = songList.groupBy { it.artistId }
                    .map { it.key }
                podcastAuthorGateway.getAll()
                    .filter { artists.contains(it.id) }
            }
    }

    override suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>) =
        withContext(schedulers.io) {
            var trackList = podcastPlaylistDao.getPlaylistTracksImpl(playlistId)
            for ((from, to) in moveList) {
                trackList.swap(from, to)
            }
            trackList = trackList.mapIndexed { index, entity -> entity.copy(idInPlaylist = index.toLong()) }
            podcastPlaylistDao.updateTrackList(trackList)
        }

}
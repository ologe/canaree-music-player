package dev.olog.data.repository.podcast

import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.id
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PodcastPlaylistEntity
import dev.olog.data.db.entities.PodcastPlaylistTrackEntity
import dev.olog.data.mapper.toDomain
import dev.olog.data.utils.assertBackground
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway
) : PodcastPlaylistGateway {

    private val podcastPlaylistDao = appDatabase.podcastPlaylistDao()
    private val historyDao = appDatabase.historyDao()

    override fun getAll(): List<Playlist> {
        assertBackgroundThread()
        val result = podcastPlaylistDao.getAllPlaylists()
        return result.map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return podcastPlaylistDao.observeAllPlaylists()
            .distinctUntilChanged()
            .asFlow()
            .mapListItem { it.toDomain() }
            .assertBackground()
    }

    override fun getByParam(param: Id): Playlist? {
        assertBackgroundThread()
        return podcastPlaylistDao.getPlaylistById(param)?.toDomain()

    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return podcastPlaylistDao.observePlaylistById(param)
            .distinctUntilChanged()
            .map { it.toDomain() }
            .asFlow()
            .assertBackground()
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        TODO()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        TODO()
    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        assertBackgroundThread()
        return podcastPlaylistDao.createPlaylist(PodcastPlaylistEntity(name = playlistName, size = 0))
    }

    override suspend fun renamePlaylist(playlistId: Id, newTitle: String) {
        return podcastPlaylistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun deletePlaylist(playlistId: Id) {
        return podcastPlaylistDao.deletePlaylist(playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Id) {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
                AutoPlaylist.HISTORY.id -> return historyDao.deleteAllPodcasts()
            }
        }
        return podcastPlaylistDao.clearPlaylist(playlistId)
    }

    override suspend fun addSongsToPlaylist(playlistId: Id, songIds: List<Long>) {
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

    override suspend fun removeFromPlaylist(playlistId: Id, idInPlaylist: Long) {
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

    override suspend fun removeDuplicated(playlistId: Id) {
        podcastPlaylistDao.removeDuplicated(playlistId)
    }

    override suspend fun insertPodcastToHistory(podcastId: Id) {
        return historyDao.insertPodcasts(podcastId)
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        TODO()
    }
}
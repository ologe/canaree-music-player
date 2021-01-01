package dev.olog.data.repository.podcast

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.track.*
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.R
import dev.olog.data.local.history.HistoryDao
import dev.olog.data.local.playlist.PodcastPlaylistDao
import dev.olog.data.local.playlist.PodcastPlaylistEntity
import dev.olog.data.local.playlist.PodcastPlaylistTrackEntity
import dev.olog.data.local.playlist.toDomain
import dev.olog.domain.entity.Favorite
import dev.olog.shared.mapListItem
import dev.olog.shared.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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

    override suspend fun getAll(): List<Playlist> {
        val result = podcastPlaylistDao.getAllPlaylists()
        return result.map(PodcastPlaylistEntity::toDomain)
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return podcastPlaylistDao.observeAllPlaylists()
            .distinctUntilChanged()
            .mapListItem(PodcastPlaylistEntity::toDomain)
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return listOf(
            createAutoPlaylist(AutoPlaylist.LAST_ADDED, autoPlaylistTitles[0]),
            createAutoPlaylist(AutoPlaylist.FAVORITE, autoPlaylistTitles[1]),
            createAutoPlaylist(AutoPlaylist.HISTORY, autoPlaylistTitles[2])
        )
    }

    private fun createAutoPlaylist(autoPlaylist: AutoPlaylist, title: String): Playlist {
        return Playlist(
            id = autoPlaylist.id,
            title = title,
            size = 0,
            isPodcast = true
        )
    }

    override suspend fun getByParam(param: Id): Playlist? {
        return if (AutoPlaylist.isAutoPlaylist(param)){
            getAllAutoPlaylists().find { it.id == param }
        } else {
            podcastPlaylistDao.getPlaylistById(param)?.toDomain()
        }
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return flow { emit(getByParam(param)) }
        }

        return podcastPlaylistDao.observePlaylistById(param)
            .map { it }
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override suspend fun getTrackListByParam(param: Id): List<Track> {
        val autoPlaylist = AutoPlaylist.fromIdOrNull(param)
        if (autoPlaylist != null){
            return getAutoPlaylistsTracks(autoPlaylist)
        }
        return podcastPlaylistDao.getPlaylistTracks(param, podcastGateway)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Track>> {
        val autoPlaylist = AutoPlaylist.fromIdOrNull(param)
        if (autoPlaylist != null) {
            return observeAutoPlaylistsTracks(autoPlaylist)
        }
        return podcastPlaylistDao.observePlaylistTracks(param, podcastGateway)
    }

    private suspend fun getAutoPlaylistsTracks(id: AutoPlaylist): List<Track> {
        return when (id){
            AutoPlaylist.LAST_ADDED -> podcastGateway.getAll()
                .sortedByDescending { it.dateAdded }
            AutoPlaylist.FAVORITE -> favoriteGateway.getPodcasts()
            AutoPlaylist.HISTORY -> historyDao.getPodcasts(podcastGateway)
        }
    }

    private fun observeAutoPlaylistsTracks(id: AutoPlaylist): Flow<List<Track>> {
        return when (id){
            AutoPlaylist.LAST_ADDED -> podcastGateway.observeAll().map { list ->
                list.sortedByDescending { it.dateAdded }
            }
            AutoPlaylist.FAVORITE -> favoriteGateway.observePodcasts()
            AutoPlaylist.HISTORY -> historyDao.observePodcasts(podcastGateway)
        }
    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != param } }
            .distinctUntilChanged()
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        return podcastPlaylistDao.createPlaylist(PodcastPlaylistEntity(name = playlistName, size = 0))
    }

    override suspend fun renamePlaylist(playlistId: Id, newTitle: String) {
        return podcastPlaylistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun deletePlaylist(playlistId: Id) {
        return podcastPlaylistDao.deletePlaylist(playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Id) {
        require(AutoPlaylist.isAutoPlaylist(playlistId))
        when (playlistId) {
            AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(Favorite.Type.PODCAST)
            AutoPlaylist.HISTORY.id -> return historyDao.deleteAllPodcasts()
        }
    }

    override suspend fun addSongsToPlaylist(playlistId: Id, vararg songIds: Long) {
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
        val autoPlaylist = AutoPlaylist.fromIdOrNull(playlistId)
        if (autoPlaylist != null) {
            return removeFromAutoPlaylist(autoPlaylist, idInPlaylist)
        }
        return podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist)
    }

    private suspend fun removeFromAutoPlaylist(autoPlaylist: AutoPlaylist, songId: Long) {
        return when (autoPlaylist) {
            AutoPlaylist.FAVORITE -> favoriteGateway.deleteSingle(Favorite.Type.PODCAST, songId)
            AutoPlaylist.HISTORY -> historyDao.deleteSinglePodcast(songId)
            AutoPlaylist.LAST_ADDED -> error("cannot remove from last added")
        }
    }

    override suspend fun removeDuplicated(playlistId: Id) {
        val notDuplicate = podcastPlaylistDao.getPlaylistTracksImpl(playlistId)
            .asSequence()
            .groupBy { it.podcastId }
            .map { it.value[0] }
            .toList()
        podcastPlaylistDao.deletePlaylistTracks(playlistId)
        podcastPlaylistDao.insertTracks(notDuplicate)
    }

    override suspend fun insertPodcastToHistory(podcastId: Id) {
        return historyDao.insertPodcasts(podcastId)
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        return observeTrackListByParam(params)
            .map {  songList ->
                val artists = songList.groupBy { it.artistId }
                    .map { it.key }
                podcastArtistGateway.getAll()
                    .filter { artists.contains(it.id) }
            }
    }

    override suspend fun moveItem(
        playlistId: Long,
        moveList: List<Pair<Int, Int>>
    ) = withContext(Dispatchers.IO) {
        val trackList = podcastPlaylistDao.getPlaylistTracksImpl(playlistId).toMutableList()
        for ((from, to) in moveList) {
            trackList.swap(from, to)
        }
        val result = trackList.mapIndexed { index, entity -> entity.copy(idInPlaylist = index.toLong()) }
        podcastPlaylistDao.updateTrackList(result)
    }

}
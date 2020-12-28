package dev.olog.data.repository.podcast

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.R
import dev.olog.data.local.history.HistoryDao
import dev.olog.data.local.playlist.PodcastPlaylistDao
import dev.olog.data.local.playlist.PodcastPlaylistEntity
import dev.olog.data.local.playlist.PodcastPlaylistTrackEntity
import dev.olog.data.local.playlist.toDomain
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
            createAutoPlaylist(AutoPlaylist.LAST_ADDED.id, autoPlaylistTitles[0]),
            createAutoPlaylist(AutoPlaylist.FAVORITE.id, autoPlaylistTitles[1]),
            createAutoPlaylist(AutoPlaylist.HISTORY.id, autoPlaylistTitles[2])
        )
    }

    private fun createAutoPlaylist(id: Long, title: String): Playlist {
        return Playlist(id, title, 0, true)
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

    override suspend fun getTrackListByParam(param: Id): List<PlaylistSong> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return getAutoPlaylistsTracks(param)
        }
        return podcastPlaylistDao.getPlaylistTracks(param, podcastGateway)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<PlaylistSong>> {
        if (AutoPlaylist.isAutoPlaylist(param)){
            return observeAutoPlaylistsTracks(param)
        }
        return podcastPlaylistDao.observePlaylistTracks(param, podcastGateway)
    }

    private suspend fun getAutoPlaylistsTracks(param: Id): List<PlaylistSong> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> podcastGateway.getAll()
                .sortedByDescending { it.dateAdded }
                .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.getPodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.getPodcasts(podcastGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
        }
    }

    private fun observeAutoPlaylistsTracks(param: Id): Flow<List<PlaylistSong>> {
        return when (param){
            AutoPlaylist.LAST_ADDED.id -> podcastGateway.observeAll().map { list ->
                list.sortedByDescending { it.dateAdded }
                    .mapIndexed { index, song -> song.toPlaylistSong(index.toLong()) }
            }
            AutoPlaylist.FAVORITE.id -> favoriteGateway.observePodcasts()
            AutoPlaylist.HISTORY.id -> historyDao.observePodcasts(podcastGateway)
            else -> throw IllegalStateException("invalid auto playlist id")
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
            AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
            AutoPlaylist.HISTORY.id -> return historyDao.deleteAllPodcasts()
        }
    }

    override suspend fun addSongsToPlaylist(playlistId: Id, songIds: List<Long>) {
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
                val artists = songList.groupBy { it.song.artistId }
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
package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.repository.MockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastPlaylistRepository @Inject constructor(

) : PodcastPlaylistGateway {

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {

    }

    override suspend fun deletePlaylist(playlistId: Long) {

    }

    override suspend fun clearPlaylist(playlistId: Long) {

    }


    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {

    }

    override suspend fun removeDuplicated(playlistId: Long) {

    }

    override suspend fun insertPodcastToHistory(podcastId: Long) {

    }

    override fun getAll(): List<Playlist> {
        return MockData.playlist(true)
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Long): Playlist? {
        return getAll().first()
    }

    override fun observeByParam(param: Long): Flow<Playlist?> {
        return flowOf(getByParam(param))
    }

    override fun getTrackListByParam(param: Long): List<Song> {
        return MockData.songs(true)
    }

    override fun observeTrackListByParam(param: Long): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeSiblings(param: Long): Flow<List<Playlist>> {
        return observeAll()
    }

    override fun observeRelatedArtists(param: Long): Flow<List<Artist>> {
        return flowOf(MockData.artist(true))
    }

    override fun getAllAutoPlaylists(): List<Playlist> {
        return MockData.autoPlaylist()
    }

    override suspend fun createPlaylist(playlistName: String): Long {
        return 1
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {

    }

    override suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>) {

    }
}
package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.data.repository.MockData
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastPlaylistRepository @Inject constructor(): PodcastPlaylistGateway {

    override fun createPlaylist(playlistName: String): Long {
        return 1
    }

    override fun renamePlaylist(playlistId: Id, newTitle: String): Completable {
        return Completable.complete()
    }

    override fun deletePlaylist(playlistId: Id): Completable {
        return Completable.complete()
    }

    override fun clearPlaylist(playlistId: Id): Completable {
        return Completable.complete()
    }

    override fun addSongsToPlaylist(playlistId: Id, songIds: List<Long>) {

    }

    override suspend fun removeFromPlaylist(playlistId: Id, idInPlaylist: Long) {

    }

    override fun removeDuplicated(playlistId: Id): Completable {
        return Completable.complete()
    }

    override fun insertPodcastToHistory(podcastId: Id): Completable {
        return Completable.complete()
    }

    override fun getAll(): List<Playlist> {
        return MockData.playlist(true)
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return flowOf(getAll())
    }

    override fun getByParam(param: Id): Playlist? {
        return getAll().first()
    }

    override fun observeByParam(param: Id): Flow<Playlist?> {
        return flowOf(getByParam(param))
    }

    override fun getTrackListByParam(param: Id): List<Song> {
        return MockData.songs(true)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        return flowOf(MockData.artist(true))
    }
}
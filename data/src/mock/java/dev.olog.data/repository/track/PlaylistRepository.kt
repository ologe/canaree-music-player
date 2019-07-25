package dev.olog.data.repository.track

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.data.repository.MockData
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PlaylistRepository @Inject constructor(): PlaylistGateway {

    override fun getAllAutoPlaylists(): List<Playlist> {
        return MockData.autoPlaylist()
    }

    override fun getAll(): List<Playlist> {
        return MockData.playlist(false)
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
        return MockData.songs(false)
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flowOf(getTrackListByParam(param))
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return observeTrackListByParam(mediaId.resolveId)
    }

    override suspend fun insertMostPlayed(mediaId: MediaId) {

    }

    override fun observeSiblings(param: Id): Flow<List<Playlist>> {
        return observeAll()
    }

    override fun createPlaylist(playlistName: String): Long {
        return 1
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return Completable.complete()
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return Completable.complete()
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {

    }

    override fun insertSongToHistory(songId: Long): Completable {
        return Completable.complete()
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return false
    }

    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {

    }

    override suspend fun removeDuplicated(playlistId: Long) {

    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        return flowOf(MockData.artist(false))
    }
}
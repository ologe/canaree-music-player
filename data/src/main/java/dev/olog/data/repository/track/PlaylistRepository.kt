package dev.olog.data.repository.track

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.data.db.dao.PlaylistMostPlayedDao
import dev.olog.data.db.entities.PlaylistMostPlayedEntity
import dev.olog.data.mediastore.MediaStoreAudioInternalDao
import dev.olog.data.mediastore.MediaStorePlaylistInternalEntity
import dev.olog.data.mediastore.MediaStorePlaylistMembersInternalEntity
import dev.olog.data.mediastore.MediaStoreQuery
import dev.olog.data.mediastore.artist.toArtist
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDirectoryRepository
import dev.olog.data.mediastore.playlist.toPlaylist
import dev.olog.data.playlist.PlaylistOperations
import dev.olog.data.queries.PlaylistsQueries
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PlaylistRepository @Inject constructor(
    private val queries: PlaylistsQueries,
    private val operations: PlaylistOperations,
    private val mostPlayedDao: PlaylistMostPlayedDao,
    private val playlistDirectoryRepository: MediaStorePlaylistDirectoryRepository,
    private val dao: MediaStoreAudioInternalDao,
    private val mediaStoreQuery: MediaStoreQuery,
) : PlaylistGateway {

    override fun getAll(mode: QueryMode): List<Playlist> {
        return queries.getAll(mode).map { it.toPlaylist() }
    }

    override fun observeAll(mode: QueryMode): Flow<List<Playlist>> {
        return queries.observeAll(mode)
            .mapListItem { it.toPlaylist() }
    }

    override fun getById(id: Long): Playlist? {
        return queries.getById(id)?.toPlaylist()
    }

    override fun observeById(id: Long): Flow<Playlist?> {
        return queries.observeById(id).map { it?.toPlaylist() }
    }

    override fun getTrackListById(mediaId: MediaId): List<Song> {
        val id = mediaId.id
        val isPodcast = mediaId.isPodcast
        return queries.getSongList(isPodcast, id)
            .map { it.toSong() }
    }

    override fun observeTrackListById(mediaId: MediaId): Flow<List<Song>> {
        val id = mediaId.id
        val isPodcast = mediaId.isPodcast
        return queries.observeSongList(isPodcast, id)
            .mapListItem { it.toSong() }
    }

    override fun observeMostPlayed(mediaId: MediaId): Flow<List<Song>> {
        return mostPlayedDao.observe(mediaId.id)
            .mapListItem { it.toSong() }
    }

    override suspend fun insertMostPlayed(
        parentMediaId: MediaId,
        mediaId: MediaId
    ) {
        val entity = PlaylistMostPlayedEntity(
            songId = mediaId.id,
            playlistId = parentMediaId.id,
        )
        mostPlayedDao.insertOne(entity)
    }

    override fun observeSiblings(mediaId: MediaId): Flow<List<Playlist>> {
        val id = mediaId.id
        val mode = if (mediaId.isPodcast) QueryMode.Podcasts else QueryMode.Songs
        return observeAll(mode)
            .filterListItem { it.id != id }
    }

    override fun observeRelatedArtists(id: Long): Flow<List<Artist>> {
        return queries.observeRelatedArtists(id)
            .mapListItem { it.toArtist() }
    }

    override suspend fun createPlaylist(title: String): Long? {
        val entity = operations.createPlaylist(title)
        if (entity != null) {
            dao.insertPlaylist(entity)
        }
        return entity?.id
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        val playlist = getById(playlistId) ?: return
        if (operations.renamePlaylist(playlist, newTitle)) {
            val updatedPlaylist = mediaStoreQuery.queryPlaylist(playlistId) ?: return
            val entity = MediaStorePlaylistInternalEntity(
                id = updatedPlaylist.id,
                title = updatedPlaylist.title,
                path = updatedPlaylist.path,
            )
            dao.insertPlaylist(entity)
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        val playlist = getById(playlistId) ?: return
        if (operations.deletePlaylist(playlist)) {
            dao.clearPlaylist(playlistId)
            dao.deletePlaylist(playlistId)
        }
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        val playlist = getById(playlistId) ?: return
        if (operations.clearPlaylist(playlist)) {
            dao.clearPlaylist(playlistId)
        }
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songs: List<Song>): Int {
        val playlist = getById(playlistId) ?: return 0
        val entities = operations.addSongsToPlaylist(playlist, songs)
        return dao.insertAllPlaylistsMembers(
            entities.map {
                MediaStorePlaylistMembersInternalEntity(
                    id = it.id,
                    audioId = it.audioId,
                    playlistId = it.playlistId,
                    playOrder = it.playOrder,
                )
            }
        ).size
    }

    override suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>) {
        // TODO playlist members play order is index 1 based
        //   the rest is index 0 based
        val playlist = getById(playlistId) ?: return
        if (operations.moveItem(playlist, moveList) > 0) {
            for ((from, to) in moveList) {
                dao.movePlaylistMembers(playlistId, from, to)
            }
        }
    }

    override suspend fun removeFromPlaylist(mediaId: MediaId, idInPlaylist: Long) {
        val playlistId = mediaId.id

        val playlist = getById(playlistId) ?: return
        if (operations.removeFromPlaylist(playlist, idInPlaylist)) {
            dao.removeFromPlaylist(playlistId, idInPlaylist)
        }
    }

    override suspend fun removeDuplicated(playlistId: Long) {
        val playlist = getById(playlistId) ?: return
        val tracks = getTrackListById(playlist.getMediaId())
        val nonDuplicated = tracks.distinctBy { it.id }
        val result = operations.overridePlaylistMembers(playlist, nonDuplicated)
        dao.overridePlaylistMembers(playlistId, result)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getPlaylistDirectory(): Uri? {
        return playlistDirectoryRepository.get()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun setPlaylistDirectory(documentUri: Uri?) {
        return playlistDirectoryRepository.set(documentUri)
    }
}
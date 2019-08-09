package dev.olog.data.repository

import dev.olog.core.entity.AutoPlaylist
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.entities.PlaylistEntity
import dev.olog.data.db.entities.PlaylistTrackEntity
import dev.olog.data.utils.assertBackgroundThread
import dev.olog.shared.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class PlaylistRepositoryHelper @Inject constructor(
    appDatabase: AppDatabase,
    private val favoriteGateway: FavoriteGateway

) : PlaylistOperations {

    private val playlistDao = appDatabase.playlistDao()
    private val historyDao = appDatabase.historyDao()

    override suspend fun createPlaylist(playlistName: String): Long {
        assertBackgroundThread()

        return playlistDao.createPlaylist(
            PlaylistEntity(name = playlistName, size = 0)
        )
    }

    override suspend fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>) {
        assertBackgroundThread()

        var maxIdInPlaylist = (playlistDao.getPlaylistMaxId(playlistId) ?: 1).toLong()
        val tracks = songIds.map {
            PlaylistTrackEntity(
                playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                trackId = it
            )
        }
        playlistDao.insertTracks(tracks)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        return playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun clearPlaylist(playlistId: Long) {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.TRACK)
                AutoPlaylist.HISTORY.id -> return historyDao.deleteAll()
            }
        }
        return playlistDao.clearPlaylist(playlistId)
    }

    override suspend fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) {
        assertBackgroundThread()

        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            removeFromAutoPlaylist(playlistId, idInPlaylist)
        } else {
            return playlistDao.deleteTrack(playlistId, idInPlaylist)
        }
    }

    private suspend fun removeFromAutoPlaylist(playlistId: Long, songId: Long) {
        return when (playlistId) {
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteType.TRACK, songId)
            AutoPlaylist.HISTORY.id -> historyDao.deleteSingle(songId)
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override suspend fun renamePlaylist(playlistId: Long, newTitle: String) {
        return playlistDao.renamePlaylist(playlistId, newTitle)
    }

    override suspend fun moveItem(playlistId: Long, moveList: List<Pair<Int, Int>>) = withContext(Dispatchers.IO) {
        var trackList = playlistDao.getPlaylistTracksImpl(playlistId)
        for ((from, to) in moveList) {
            trackList.swap(from, to)
        }
        trackList = trackList.mapIndexed { index, entity -> entity.copy(idInPlaylist = index.toLong()) }
        playlistDao.updateTrackList(trackList)
    }

    override suspend fun removeDuplicated(playlistId: Long) {
        playlistDao.removeDuplicated(playlistId)
    }

    override suspend fun insertSongToHistory(songId: Long) {
        return historyDao.insert(songId)
    }

}
package dev.olog.data.repository.podcast

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
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
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.extensions.mapListItem
import dev.olog.shared.utils.assertBackgroundThread
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.flow.asFlow
import javax.inject.Inject

internal class PodcastPlaylistRepository @Inject constructor(
        @ApplicationContext context: Context,
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
        return listOf()
    }

    override fun observeTrackListByParam(param: Id): Flow<List<Song>> {
        return flowOf(listOf())
    }

    override fun observeSiblings(id: Id): Flow<List<Playlist>> {
        return observeAll()
            .map { it.filter { it.id != id } }
            .distinctUntilChanged()
            .assertBackground()
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return Single.fromCallable {
            podcastPlaylistDao.createPlaylist(
                PodcastPlaylistEntity(name = playlistName, size = 0)
            )
        }
    }

    override fun renamePlaylist(playlistId: Id, newTitle: String): Completable {
        return Completable.fromCallable { podcastPlaylistDao.renamePlaylist(playlistId, newTitle) }
    }

    override fun deletePlaylist(playlistId: Id): Completable {
        return Completable.fromCallable { podcastPlaylistDao.deletePlaylist(playlistId) }
    }

    override fun clearPlaylist(playlistId: Id): Completable {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            when (playlistId) {
                AutoPlaylist.FAVORITE.id -> return favoriteGateway.deleteAll(FavoriteType.PODCAST)
                AutoPlaylist.HISTORY.id -> return Completable.fromCallable { historyDao.deleteAllPodcasts() }
            }
        }
        return Completable.fromCallable { podcastPlaylistDao.clearPlaylist(playlistId) }
    }

    override fun addSongsToPlaylist(playlistId: Id, songIds: List<Long>): Completable {
        return Completable.fromCallable {
            var maxIdInPlaylist = podcastPlaylistDao.getPlaylistMaxId(playlistId).toLong()
            val tracks = songIds.map {
                PodcastPlaylistTrackEntity(
                    playlistId = playlistId, idInPlaylist = ++maxIdInPlaylist,
                    podcastId = it
                )
            }
            podcastPlaylistDao.insertTracks(tracks)
        }
    }

    override fun removeSongFromPlaylist(playlistId: Id, idInPlaylist: Long): Completable {
        if (AutoPlaylist.isAutoPlaylist(playlistId)) {
            return removeFromAutoPlaylist(playlistId, idInPlaylist)
        }
        return Completable.fromCallable { podcastPlaylistDao.deleteTrack(playlistId, idInPlaylist) }
    }

    private fun removeFromAutoPlaylist(playlistId: Long, songId: Long): Completable {
        return when (playlistId) {
            AutoPlaylist.FAVORITE.id -> favoriteGateway.deleteSingle(FavoriteType.PODCAST, songId)
            AutoPlaylist.HISTORY.id -> Completable.fromCallable {
                historyDao.deleteSinglePodcast(
                    songId
                )
            }
            else -> throw IllegalArgumentException("invalid auto playlist id: $playlistId")
        }
    }

    override fun removeDuplicated(playlistId: Id): Completable {
        return Completable.fromCallable { podcastPlaylistDao.removeDuplicated(playlistId) }
    }

    override fun insertPodcastToHistory(podcastId: Id): Completable {
        return historyDao.insertPodcasts(podcastId)
    }

    override fun observeRelatedArtists(params: Id): Flow<List<Artist>> {
        TODO()
    }
}
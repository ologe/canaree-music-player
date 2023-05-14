package dev.olog.data.repository.track

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaId
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.entity.track.AutoPlaylist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.AutoPlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.R
import dev.olog.data.db.dao.HistoryDao
import dev.olog.data.db.entities.HistoryEntity
import dev.olog.data.db.entities.PodcastHistoryEntity
import dev.olog.data.mediastore.audio.toSong
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class AutoPlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val favoriteGateway: FavoriteGateway,
    private val historyDao: HistoryDao,
) : AutoPlaylistGateway {

    override fun observeAll(mode: QueryMode): Flow<List<AutoPlaylist>> = when (mode) {
        QueryMode.All -> combine(
            observeAll(QueryMode.Songs),
            observeAll(QueryMode.Podcasts),
        ) { a, b -> a + b }
        QueryMode.Songs -> createObservableAutoPlaylists(false)
        QueryMode.Podcasts -> createObservableAutoPlaylists(true)
    }

    override fun getById(id: Long): AutoPlaylist? {
        val playlistId = AutoPlaylist.findPlaylistId(id) ?: return null
        return when (playlistId) {
            AutoPlaylist.Id.SongLastAdded -> createLastAddedPlaylist(false)
            AutoPlaylist.Id.SongFavorites -> createFavouritePlaylist(false)
            AutoPlaylist.Id.SongHistory -> createHistoryPlaylist(false)
            AutoPlaylist.Id.PodcastLastAdded -> createLastAddedPlaylist(true)
            AutoPlaylist.Id.PodcastFavorites -> createFavouritePlaylist(true)
            AutoPlaylist.Id.PodcastHistory -> createHistoryPlaylist(true)
        }
    }

    override fun observeById(id: Long): Flow<AutoPlaylist?> {
        return observeAll(QueryMode.All)
            .mapNotNull { list ->
                list.find { it.id.key == id }
            }
    }

    override fun getTrackListById(id: Long): List<Song> {
        val playlistId = AutoPlaylist.findPlaylistId(id) ?: return emptyList()
        return when (playlistId) {
            AutoPlaylist.Id.SongLastAdded -> songGateway.getAll().sortedByDescending { it.dateAdded }
            AutoPlaylist.Id.SongFavorites -> favoriteGateway.getTracks()
            AutoPlaylist.Id.SongHistory -> historyDao.getAllTracks().map { it.toSong() }
            AutoPlaylist.Id.PodcastLastAdded -> podcastGateway.getAll().sortedByDescending { it.dateAdded }
            AutoPlaylist.Id.PodcastFavorites -> favoriteGateway.getPodcasts()
            AutoPlaylist.Id.PodcastHistory -> historyDao.getAllPodcasts().map { it.toSong() }
        }
    }

    override fun observeTrackListById(id: Long): Flow<List<Song>> {
        val playlistId = AutoPlaylist.findPlaylistId(id) ?: return flowOf(emptyList())
        return when (playlistId) {
            AutoPlaylist.Id.SongLastAdded -> songGateway.observeAll().map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.Id.SongFavorites -> favoriteGateway.observeTracks()
            AutoPlaylist.Id.SongHistory -> historyDao.observeAllTracks().mapListItem { it.toSong() }
            AutoPlaylist.Id.PodcastLastAdded -> podcastGateway.observeAll().map { it.sortedByDescending { it.dateAdded } }
            AutoPlaylist.Id.PodcastFavorites -> favoriteGateway.observePodcasts()
            AutoPlaylist.Id.PodcastHistory -> historyDao.observeAllPodcasts().mapListItem { it.toSong() }
        }
    }

    override suspend fun clearPlaylist(id: Long) {
        val playlistId = AutoPlaylist.findPlaylistId(id) ?: return
        when (playlistId) {
            AutoPlaylist.Id.SongFavorites -> favoriteGateway.deleteAll(FavoriteType.TRACK)
            AutoPlaylist.Id.SongHistory -> historyDao.deleteAllSongs()
            AutoPlaylist.Id.PodcastFavorites -> favoriteGateway.deleteAll(FavoriteType.PODCAST)
            AutoPlaylist.Id.PodcastHistory -> historyDao.deleteAllPodcasts()
            else -> {
                // TODO log invalid state?
            }
        }
    }

    override suspend fun removeFromAutoPlaylist(mediaId: MediaId, trackId: Long) {
        val playlistId = AutoPlaylist.findPlaylistId(mediaId.id) ?: return
        return when (playlistId) {
            AutoPlaylist.Id.PodcastFavorites -> favoriteGateway.deleteSingle(FavoriteType.PODCAST, trackId)
            AutoPlaylist.Id.PodcastHistory -> historyDao.deletePodcast(trackId)
            AutoPlaylist.Id.SongFavorites -> favoriteGateway.deleteSingle(FavoriteType.TRACK, trackId)
            AutoPlaylist.Id.SongHistory -> historyDao.deleteSong(trackId)
            else -> error("remove not supported for auto playlist with id:$playlistId")
        }
    }

    override suspend fun insertToHistory(mediaId: MediaId) {
        if (mediaId.isPodcast) {
            historyDao.insertPodcast(PodcastHistoryEntity(podcastId = mediaId.id))
        } else {
            historyDao.insertSong(HistoryEntity(songId = mediaId.id))
        }
    }

    private fun createObservableAutoPlaylists(isPodcast: Boolean): Flow<List<AutoPlaylist>> {
        return combine(
            (if (isPodcast) podcastGateway.observeAll() else songGateway.observeAll()).map { it.size },
            (if (isPodcast) favoriteGateway.observePodcasts() else favoriteGateway.observeTracks()).map { it.size },
            (if (isPodcast) historyDao.observeAllPodcasts() else historyDao.observeAllTracks()).map { it.size },
        ) {  lastAddedSize, favoritesSize, historySize ->
            listOf(
                createLastAddedPlaylist(isPodcast, lastAddedSize),
                createFavouritePlaylist(isPodcast, favoritesSize),
                createHistoryPlaylist(isPodcast, historySize),
            )
        }
    }

    private fun createLastAddedPlaylist(
        isPodcast: Boolean,
        size: Int = if (isPodcast) podcastGateway.getAll().size else songGateway.getAll().size,
    ): AutoPlaylist {
        return AutoPlaylist(
            id = if (isPodcast) AutoPlaylist.Id.PodcastLastAdded else AutoPlaylist.Id.SongLastAdded,
            title = context.getString(R.string.common_last_added),
            size = size,
        )
    }

    private fun createFavouritePlaylist(
        isPodcast: Boolean,
        size: Int = if (isPodcast) favoriteGateway.getPodcasts().size else favoriteGateway.getTracks().size,
    ): AutoPlaylist {
        return AutoPlaylist(
            id = if (isPodcast) AutoPlaylist.Id.PodcastFavorites else AutoPlaylist.Id.SongFavorites,
            title = context.getString(R.string.common_favorites),
            size = size,
        )
    }

    private fun createHistoryPlaylist(
        isPodcast: Boolean,
        size: Int = if (isPodcast) historyDao.getAllPodcasts().size  else historyDao.getAllTracks().size,
    ): AutoPlaylist {
        return AutoPlaylist(
            id = if (isPodcast) AutoPlaylist.Id.PodcastHistory else AutoPlaylist.Id.SongHistory,
            title = context.getString(R.string.common_history),
            size = size,
        )
    }

}
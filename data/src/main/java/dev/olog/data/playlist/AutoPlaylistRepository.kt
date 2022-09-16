package dev.olog.data.playlist

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.data.R
import dev.olog.data.db.history.HistoryDao
import dev.olog.data.mediastore.podcast.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.podcast.PodcastDao
import dev.olog.data.song.SongDao
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoPlaylistRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songDao: SongDao, // todo use gateway
    private val podcastDao: PodcastDao, // todo use gateway
    private val historyDao: HistoryDao, // todo use gateway
    private val favouritesGateway: FavoriteGateway,
) {

    fun getAll(isPodcast: Boolean): List<Playlist> {
        return listOf(
            lastAddedPlaylist(isPodcast),
            favouritesPlaylist(isPodcast),
            historyPlaylist(isPodcast),
        )
    }

    fun observeAll(isPodcast: Boolean): Flow<List<Playlist>> {
        return combine(
            observeLastAddedPlaylist(isPodcast),
            observeFavouritesPlaylist(isPodcast),
            observeHistoryPlaylist(isPodcast),
        ) { lastAdded, favourites, history ->
            listOf(lastAdded, favourites, history)
        }
    }

    fun lastAddedPlaylist(isPodcast: Boolean): Playlist {
        return Playlist(
            id = PlaylistResolver.LastAddedId,
            title = context.getString(R.string.common_last_added),
            path = android.provider.MediaStore.UNKNOWN_STRING,
            size = if (isPodcast) podcastDao.countAll() else songDao.countAll(),
            isPodcast = isPodcast
        )
    }

    fun observeLastAddedPlaylist(isPodcast: Boolean): Flow<Playlist> {
        return (if (isPodcast) podcastDao.observeCountAll() else songDao.observeCountAll())
            .map { size ->
                Playlist(
                    id = PlaylistResolver.LastAddedId,
                    title = context.getString(R.string.common_last_added),
                    path = android.provider.MediaStore.UNKNOWN_STRING,
                    size = size,
                    isPodcast = isPodcast
                )
            }
    }

    fun favouritesPlaylist(isPodcast: Boolean): Playlist {
        val size = if (isPodcast) favouritesGateway.getPodcastsCount() else favouritesGateway.getTracksCount()
        return Playlist(
            id = PlaylistResolver.FavouritesId,
            title = context.getString(R.string.common_favorites),
            path = android.provider.MediaStore.UNKNOWN_STRING,
            size = size,
            isPodcast = isPodcast
        )
    }

    fun observeFavouritesPlaylist(isPodcast: Boolean): Flow<Playlist> {
        return (if (isPodcast) favouritesGateway.observePodcastsCount() else favouritesGateway.observeTracksCount())
            .map { size ->
                Playlist(
                    id = PlaylistResolver.FavouritesId,
                    title = context.getString(R.string.common_favorites),
                    path = android.provider.MediaStore.UNKNOWN_STRING,
                    size = size,
                    isPodcast = isPodcast
                )
            }
    }

    fun historyPlaylist(isPodcast: Boolean): Playlist {
        val size: Int = if (isPodcast) historyDao.countAllPodcasts() else historyDao.countAllSongs()
        return Playlist(
            id = PlaylistResolver.FavouritesId,
            title = context.getString(R.string.common_history),
            path = android.provider.MediaStore.UNKNOWN_STRING,
            size = size,
            isPodcast = isPodcast
        )
    }

    fun observeHistoryPlaylist(isPodcast: Boolean): Flow<Playlist> {
        return (if (isPodcast) historyDao.observeCountAllPodcasts() else historyDao.observeCountAllSongs())
            .map { size ->
                Playlist(
                    id = PlaylistResolver.FavouritesId,
                    title = context.getString(R.string.common_history),
                    path = android.provider.MediaStore.UNKNOWN_STRING,
                    size = size,
                    isPodcast = isPodcast
                )
            }
    }

    fun getLastAddedTrackList(isPodcast: Boolean): List<Song> {
        return when (isPodcast) {
            true -> songDao.getAll().map { it.toDomain() }
            false -> podcastDao.getAll().map { it.toDomain() }
        }.sortedByDescending { it.dateAdded }
    }

    fun observeLastAddedTrackList(isPodcast: Boolean): Flow<List<Song>> {
        return when (isPodcast) {
            true -> songDao.observeAll().mapListItem { it.toDomain() }
            false -> podcastDao.observeAll().mapListItem { it.toDomain() }
        }.map { list ->
            list.sortedByDescending { it.dateAdded }
        }
    }

    fun getFavouritesTracksList(isPodcast: Boolean): List<Song> {
        return when (isPodcast) {
            true -> favouritesGateway.getTracks()
            false -> favouritesGateway.getPodcasts()
        }
    }

    fun observeFavouritesTracksList(isPodcast: Boolean): Flow<List<Song>> {
        return when (isPodcast) {
            true -> favouritesGateway.observeTracks()
            false -> favouritesGateway.observePodcasts()
        }
    }

    fun getHistoryTracksList(isPodcast: Boolean): List<Song> {
        TODO()
//        return when (isPodcast) {
//            true -> historyDao.getTracks()
//            false -> historyDao.getPodcasts()
//        }
    }

    fun observeHistoryTracksList(isPodcast: Boolean): Flow<List<Song>> {
        TODO()
//        return when (isPodcast) {
//            true -> historyDao.observeTracks()
//            false -> historyDao.observePodcasts()
//        }
    }

}
package dev.olog.data.song.playlist

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Playlist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.PlaylistOperations
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.db.most.played.PlaylistMostPlayedDao
import dev.olog.data.db.most.played.PlaylistMostPlayedEntity
import dev.olog.data.mediastore.song.playlist.toDomain
import dev.olog.data.playlist.AutoPlaylistRepository
import dev.olog.data.playlist.PlaylistResolver
import dev.olog.data.repository.PlaylistRepositoryHelper
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dev.olog.data.song.playlist.LegacyPlaylistDao as PlaylistDaoLegacy

internal class PlaylistRepository @Inject constructor(
    private val autoPlaylistRepository: AutoPlaylistRepository,
    private val playlistDao: PlaylistDao,
    private val songGateway: SongGateway,
    private val artistGateway: ArtistGateway,
    private val helper: PlaylistRepositoryHelper,
    private val mostPlayedDao: PlaylistMostPlayedDao,
    private val playlistDaoLegacy: PlaylistDaoLegacy
) : PlaylistGateway, PlaylistOperations by helper {

    override fun getAllAutoPlaylists(): List<Playlist> {
        return autoPlaylistRepository.getAll(false)
    }

    override fun getAll(): List<Playlist> {
        return buildList {
            addAll(getAllAutoPlaylists())
            addAll(playlistDao.getAll().map { it.toDomain() })
        }
    }

    override fun observeAll(): Flow<List<Playlist>> {
        return combine(
            autoPlaylistRepository.observeAll(false).distinctUntilChanged(),
            playlistDao.observeAll().distinctUntilChanged().mapListItem { it.toDomain() }
        ) { auto, mediastore -> auto + mediastore }
    }

    override fun getByParam(id: String): Playlist? {
        return when (PlaylistResolver.fromId(id)) {
            is PlaylistResolver.LastAdded -> autoPlaylistRepository.lastAddedPlaylist(false)
            is PlaylistResolver.Favourites -> autoPlaylistRepository.favouritesPlaylist(false)
            is PlaylistResolver.History -> autoPlaylistRepository.historyPlaylist(false)
            is PlaylistResolver.MediaStore -> playlistDao.getById(id)?.toDomain()
        }
    }

    override fun observeByParam(id: String): Flow<Playlist?> {
        return when (PlaylistResolver.fromId(id)) {
            is PlaylistResolver.LastAdded -> autoPlaylistRepository.observeLastAddedPlaylist(false)
            is PlaylistResolver.Favourites -> autoPlaylistRepository.observeFavouritesPlaylist(false)
            is PlaylistResolver.History -> autoPlaylistRepository.observeHistoryPlaylist(false)
            is PlaylistResolver.MediaStore -> playlistDao.observeById(id).map { it?.toDomain() }
        }
    }

    override fun getTrackListByParam(id: String): List<Song> {
        return when (PlaylistResolver.fromId(id)) {
            is PlaylistResolver.LastAdded -> autoPlaylistRepository.getLastAddedTrackList(false)
            is PlaylistResolver.Favourites -> autoPlaylistRepository.getFavouritesTracksList(false)
            is PlaylistResolver.History -> autoPlaylistRepository.getHistoryTracksList(false)
            is PlaylistResolver.MediaStore -> TODO()
        }
    }

    override fun observeTrackListByParam(id: String): Flow<List<Song>> {
        return when (PlaylistResolver.fromId(id)) {
            is PlaylistResolver.LastAdded -> autoPlaylistRepository.observeLastAddedTrackList(false)
            is PlaylistResolver.Favourites -> autoPlaylistRepository.observeFavouritesTracksList(false)
            is PlaylistResolver.History -> autoPlaylistRepository.observeHistoryTracksList(false)
            is PlaylistResolver.MediaStore -> TODO()
        }
    }

    private fun getAutoPlaylistsTracks(id: Long): List<Song> {
        TODO()
//        return when (id){
//            AutoPlaylist.LAST_ADDED.id -> songGateway.getAll().sortedByDescending { it.dateAdded }
//            AutoPlaylist.FAVORITE.id -> favoriteGateway.getTracks()
//            AutoPlaylist.HISTORY.id -> historyDao.getTracks(songGateway)
//            else -> throw IllegalStateException("invalid auto playlist id")
//        }
    }

    override fun observeMostPlayed(id: String): Flow<List<Song>> {
        TODO()
//        val folderPath = mediaId.categoryId
//        return mostPlayedDao.getAll(folderPath, songGateway)
//            .distinctUntilChanged()
    }

    override suspend fun insertMostPlayed(playlistId: String, songId: String) {
        TODO()
//        mostPlayedDao.insertOne(
//            PlaylistMostPlayedEntity(
//                0,
//                mediaId.leaf!!,
//                mediaId.categoryId
//            )
//        )
    }

    override fun observeSiblings(id: String): Flow<List<Playlist>> {
        TODO()
//        return observeAll()
//            .map { it.filter { it.id != id } }
//            .distinctUntilChanged()
    }

    override fun observeRelatedArtists(id: String): Flow<List<Artist>> {
        TODO()
//        return observeTrackListByParam(id)
//            .map {  songList ->
//                val artists = songList.groupBy { it.artistId }
//                    .map { it.key }
//                artistGateway.getAll()
//                    .filter { artists.contains(it.id) }
//            }
    }

}
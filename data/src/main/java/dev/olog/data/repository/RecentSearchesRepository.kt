package dev.olog.data.repository

import dev.olog.core.MediaId
import dev.olog.core.entity.SearchResult
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.data.db.RecentSearchesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class RecentSearchesRepository @Inject constructor(
    private val dao: RecentSearchesDao,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val folderGateway: FolderGateway,

    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway

) : RecentSearchesGateway {

    override fun getAll(): Flow<List<SearchResult>> {
        return dao.getAll(
            trackGateway,
            albumGateway,
            artistGateway,
            playlistGateway,
            genreGateway,
            folderGateway,
            podcastPlaylistGateway,
            podcastAuthorGateway
        )
    }

    override suspend fun insertTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return dao.insertPodcast(mediaId.id)
        }
        return dao.insertSong(mediaId.id)
    }
    override suspend fun insertAlbum(albumId: Long) = dao.insertAlbum(albumId)
    override suspend fun insertArtist(artistId: Long) = dao.insertArtist(artistId)
    override suspend fun insertPlaylist(playlistId: Long) = dao.insertPlaylist(playlistId)
    override suspend fun insertGenre(genreId: Long) = dao.insertGenre(genreId)
    override suspend fun insertFolder(folderId: Long) = dao.insertFolder(folderId)

    override suspend fun insertPodcastPlaylist(playlistid: Long) = dao.insertPodcastPlaylist(playlistid)
    override suspend fun insertPodcastArtist(artistId: Long) = dao.insertPodcastArtist(artistId)

    override suspend fun deleteTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return dao.deletePodcast(mediaId.id)
        }
        return dao.deleteSong(mediaId.id)
    }
    override suspend fun deleteAlbum(itemId: Long) = dao.deleteAlbum(itemId)
    override suspend fun deleteArtist(itemId: Long) = dao.deleteArtist(itemId)
    override suspend fun deletePlaylist(itemId: Long) = dao.deletePlaylist(itemId)
    override suspend fun deleteFolder(itemId: Long) = dao.deleteFolder(itemId)
    override suspend fun deleteGenre(itemId: Long) = dao.deleteGenre(itemId)

    override suspend fun deletePodcastPlaylist(playlistId: Long) = dao.deletePodcastPlaylist(playlistId)
    override suspend fun deletePodcastArtist(artistId: Long) = dao.deletePodcastArtist(artistId)

    override suspend fun deleteAll() = dao.deleteAll()
}
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
            return dao.insertPodcast(mediaId.id.toString())
        }
        return dao.insertSong(mediaId.id.toString())
    }
    override suspend fun insertAlbum(albumId: String) = dao.insertAlbum(albumId)
    override suspend fun insertArtist(artistId: String) = dao.insertArtist(artistId)
    override suspend fun insertPlaylist(playlistId: String) = dao.insertPlaylist(playlistId)
    override suspend fun insertGenre(genreId: String) = dao.insertGenre(genreId)
    override suspend fun insertFolder(folderId: String) = dao.insertFolder(folderId)

    override suspend fun insertPodcastPlaylist(playlistid: String) = dao.insertPodcastPlaylist(playlistid)
    override suspend fun insertPodcastArtist(artistId: String) = dao.insertPodcastArtist(artistId)

    override suspend fun deleteTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return dao.deletePodcast(mediaId.id.toString())
        }
        return dao.deleteSong(mediaId.id.toString())
    }
    override suspend fun deleteAlbum(itemId: String) = dao.deleteAlbum(itemId)
    override suspend fun deleteArtist(itemId: String) = dao.deleteArtist(itemId)
    override suspend fun deletePlaylist(itemId: String) = dao.deletePlaylist(itemId)
    override suspend fun deleteFolder(itemId: String) = dao.deleteFolder(itemId)
    override suspend fun deleteGenre(itemId: String) = dao.deleteGenre(itemId)

    override suspend fun deletePodcastPlaylist(playlistId: String) = dao.deletePodcastPlaylist(playlistId)
    override suspend fun deletePodcastArtist(artistId: String) = dao.deletePodcastArtist(artistId)

    override suspend fun deleteAll() = dao.deleteAll()
}
package dev.olog.data.repository

import dev.olog.core.entity.SearchResult
import dev.olog.core.gateway.*
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.*
import dev.olog.data.db.dao.RecentSearchesDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class RecentSearchesRepository @Inject constructor(
    private val dao: RecentSearchesDao,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val folderGateway: FolderGateway,

    private val podcastGateway: PodcastGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway

) : RecentSearchesGateway {

    override fun getAll(): Flow<List<SearchResult>> {
        return dao.getAll(
            songGateway,
            albumGateway,
            artistGateway,
            playlistGateway,
            genreGateway,
            folderGateway,
            podcastGateway,
            podcastAlbumGateway,
            podcastArtistGateway
        )
    }

    override suspend fun insertSong(songId: Long) = dao.insertSong(songId)
    override suspend fun insertAlbum(albumId: Long) = dao.insertAlbum(albumId)
    override suspend fun insertArtist(artistId: Long) = dao.insertArtist(artistId)
    override suspend fun insertPlaylist(playlistId: Long) = dao.insertPlaylist(playlistId)
    override suspend fun insertGenre(genreId: Long) = dao.insertGenre(genreId)
    override suspend fun insertFolder(folderId: Long) = dao.insertFolder(folderId)

    override suspend fun insertPodcast(podcastId: Long) = dao.insertPodcast(podcastId)
    override suspend fun insertPodcastPlaylist(playlistid: Long) = dao.insertPodcastPlaylist(playlistid)
    override suspend fun insertPodcastAlbum(albumId: Long) = dao.insertPodcastAlbum(albumId)
    override suspend fun insertPodcastArtist(artistId: Long) = dao.insertPodcastArtist(artistId)

    override suspend fun deleteSong(itemId: Long) = dao.deleteSong(itemId)
    override suspend fun deleteAlbum(itemId: Long) = dao.deleteAlbum(itemId)
    override suspend fun deleteArtist(itemId: Long) = dao.deleteArtist(itemId)
    override suspend fun deletePlaylist(itemId: Long) = dao.deletePlaylist(itemId)
    override suspend fun deleteFolder(itemId: Long) = dao.deleteFolder(itemId)
    override suspend fun deleteGenre(itemId: Long) = dao.deleteGenre(itemId)

    override suspend fun deletePodcast(podcastId: Long) = dao.deletePodcast(podcastId)
    override suspend fun deletePodcastPlaylist(playlistId: Long) = dao.deletePodcastPlaylist(playlistId)
    override suspend fun deletePodcastAlbum(albumId: Long) = dao.deletePodcastAlbum(albumId)
    override suspend fun deletePodcastArtist(artistId: Long) = dao.deletePodcastArtist(artistId)

    override suspend fun deleteAll() = dao.deleteAll()
}
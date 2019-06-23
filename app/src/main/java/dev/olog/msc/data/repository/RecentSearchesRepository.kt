package dev.olog.msc.data.repository

import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.db.dao.RecentSearchesDao
import dev.olog.core.entity.SearchResult
import dev.olog.core.gateway.*
import dev.olog.msc.domain.gateway.*
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.rx2.asFlowable
import javax.inject.Inject

class RecentSearchesRepository @Inject constructor(
    appDatabase: AppDatabase,
    private val songGateway: SongGateway2,
    private val albumGateway: AlbumGateway2,
    private val artistGateway: ArtistGateway2,
    private val playlistGateway: PlaylistGateway2,
    private val genreGateway: GenreGateway2,
    private val folderGateway: FolderGateway2,

    private val podcastGateway: PodcastGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway

) : RecentSearchesGateway {

    private val dao : RecentSearchesDao = appDatabase.recentSearchesDao()

    override fun getAll(): Observable<List<SearchResult>> {
        return dao.getAll(songGateway.observeAll().asFlowable().firstOrError(),
                albumGateway.observeAll().asFlowable().firstOrError(),
                artistGateway.observeAll().asFlowable().firstOrError(),
                playlistGateway.observeAll().asFlowable().firstOrError(),
                genreGateway.observeAll().asFlowable().firstOrError(),
                folderGateway.observeAll().asFlowable().firstOrError(),
                podcastGateway.getAll().firstOrError(),
                podcastPlaylistGateway.getAll().firstOrError(),
                podcastAlbumGateway.getAll().firstOrError(),
                podcastArtistGateway.getAll().firstOrError()
        )
    }

    override fun insertSong(songId: Long): Completable = dao.insertSong(songId)
    override fun insertAlbum(albumId: Long): Completable = dao.insertAlbum(albumId)
    override fun insertArtist(artistId: Long): Completable = dao.insertArtist(artistId)
    override fun insertPlaylist(playlistId: Long): Completable = dao.insertPlaylist(playlistId)
    override fun insertGenre(genreId: Long): Completable = dao.insertGenre(genreId)
    override fun insertFolder(folderId: Long): Completable = dao.insertFolder(folderId)

    override fun insertPodcast(podcastId: Long): Completable = dao.insertPodcast(podcastId)
    override fun insertPodcastPlaylist(playlistid: Long): Completable = dao.insertPodcastPlaylist(playlistid)
    override fun insertPodcastAlbum(albumId: Long): Completable = dao.insertPodcastAlbum(albumId)
    override fun insertPodcastArtist(artistId: Long): Completable = dao.insertPodcastArtist(artistId)

    override fun deleteSong(itemId: Long): Completable = dao.deleteSong(itemId)
    override fun deleteAlbum(itemId: Long): Completable = dao.deleteAlbum(itemId)
    override fun deleteArtist(itemId: Long): Completable = dao.deleteArtist(itemId)
    override fun deletePlaylist(itemId: Long): Completable = dao.deletePlaylist(itemId)
    override fun deleteFolder(itemId: Long): Completable = dao.deleteFolder(itemId)
    override fun deleteGenre(itemId: Long): Completable = dao.deleteGenre(itemId)

    override fun deletePodcast(podcastId: Long): Completable = dao.deletePodcast(podcastId)
    override fun deletePodcastPlaylist(playlistId: Long): Completable = dao.deletePodcastPlaylist(playlistId)
    override fun deletePodcastAlbum(albumId: Long): Completable = dao.deletePodcastAlbum(albumId)
    override fun deletePodcastArtist(artistId: Long): Completable = dao.deletePodcastArtist(artistId)

    override fun deleteAll(): Completable = dao.deleteAll()
}
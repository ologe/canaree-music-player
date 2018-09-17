package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.RecentSearchesDao
import dev.olog.msc.domain.entity.SearchResult
import dev.olog.msc.domain.gateway.*
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class RecentSearchesRepository @Inject constructor(
        appDatabase: AppDatabase,
        private val songGateway: SongGateway,
        private val albumGateway: AlbumGateway,
        private val artistGateway: ArtistGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway,
        private val folderGateway: FolderGateway

) : RecentSearchesGateway {

    private val dao : RecentSearchesDao = appDatabase.recentSearchesDao()

    override fun getAll(): Observable<List<SearchResult>> {
        return dao.getAll(songGateway.getAll().firstOrError(),
                albumGateway.getAll().firstOrError(),
                artistGateway.getAll().firstOrError(),
                playlistGateway.getAll().firstOrError(),
                genreGateway.getAll().firstOrError(),
                folderGateway.getAll().firstOrError()
        )
    }

    override fun insertSong(songId: Long): Completable = dao.insertSong(songId)

    override fun insertAlbum(albumId: Long): Completable = dao.insertAlbum(albumId)

    override fun insertArtist(artistId: Long): Completable = dao.insertArtist(artistId)

    override fun insertPlaylist(playlistId: Long): Completable = dao.insertPlaylist(playlistId)

    override fun insertGenre(genreId: Long): Completable = dao.insertGenre(genreId)

    override fun insertFolder(folderId: Long): Completable = dao.insertFolder(folderId)

    override fun deleteSong(itemId: Long): Completable = dao.deleteSong(itemId)

    override fun deleteAlbum(itemId: Long): Completable = dao.deleteAlbum(itemId)

    override fun deleteArtist(itemId: Long): Completable = dao.deleteArtist(itemId)

    override fun deletePlaylist(itemId: Long): Completable = dao.deletePlaylist(itemId)

    override fun deleteFolder(itemId: Long): Completable = dao.deleteFolder(itemId)

    override fun deleteGenre(itemId: Long): Completable = dao.deleteGenre(itemId)

    override fun deleteAll(): Completable = dao.deleteAll()
}
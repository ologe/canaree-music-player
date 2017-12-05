package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.data.db.RecentSearchesDao
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.gateway.AlbumGateway
import dev.olog.domain.gateway.ArtistGateway
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentSearchesRepository @Inject constructor(
        appDatabase: AppDatabase,
        private val songGateway: SongGateway,
        private val albumGateway: AlbumGateway,
        private val artistGateway: ArtistGateway

) : RecentSearchesGateway {

    private val dao : RecentSearchesDao = appDatabase.recentSearchesDao()

    override fun getAll(): Flowable<List<SearchResult>> {
        return dao.getAll(songGateway.getAll().firstOrError(),
                albumGateway.getAll().firstOrError(),
                artistGateway.getAll().firstOrError())
    }

    override fun insertSong(songId: Long): Completable = dao.insertSong(songId)

    override fun insertAlbum(albumId: Long): Completable = dao.insertAlbum(albumId)

    override fun insertArtist(artistId: Long): Completable = dao.insertArtist(artistId)

    override fun deleteSong(itemId: Long): Completable = dao.deleteSong(itemId)

    override fun deleteAlbum(itemId: Long): Completable = dao.deleteAlbum(itemId)

    override fun deleteArtist(itemId: Long): Completable = dao.deleteArtist(itemId)

    override fun deleteAll(): Completable = dao.deleteAll()
}
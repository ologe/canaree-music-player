package dev.olog.msc.data.repository

import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.db.RecentSearchesDao
import dev.olog.msc.domain.entity.SearchResult
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.gateway.RecentSearchesGateway
import dev.olog.msc.domain.gateway.SongGateway
import io.reactivex.Completable
import io.reactivex.Observable
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

    override fun getAll(): Observable<List<SearchResult>> {
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
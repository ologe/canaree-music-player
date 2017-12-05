package dev.olog.data.repository

import dev.olog.data.db.AppDatabase
import dev.olog.data.db.RecentSearchesDao
import dev.olog.domain.entity.Album
import dev.olog.domain.entity.Artist
import dev.olog.domain.entity.SearchResult
import dev.olog.domain.entity.Song
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

    override fun insertSong(song: Song): Completable = dao.insertSong(song)

    override fun insertAlbum(album: Album): Completable = dao.insertAlbum(album)

    override fun insertArtist(artist: Artist): Completable = dao.insertArtist(artist)

    override fun deleteItem(dataType: Int, itemId: Long): Completable {
        return dao.delete(dataType, itemId)
    }

    override fun deleteAll(): Completable = dao.deleteAll()
}
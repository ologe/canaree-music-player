package dev.olog.data.repository.track

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.data.db.dao.LastPlayedAlbumDao
import dev.olog.data.db.entities.LastPlayedAlbumEntity
import dev.olog.data.mediastore.album.toAlbum
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.AlbumsQueries
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlbumRepository @Inject constructor(
    private val queries: AlbumsQueries,
    private val lastPlayedDao: LastPlayedAlbumDao,
) : AlbumGateway {

    override fun getAll(): List<Album> {
        return queries.getAll(false).map { it.toAlbum() }
    }

    override fun observeAll(): Flow<List<Album>> {
        return queries.observeAll(false)
            .mapListItem { it.toAlbum() }
    }

    override fun getById(id: Long): Album? {
        return queries.getById(id)?.toAlbum()
    }

    override fun observeById(id: Long): Flow<Album?> {
        return queries.observeById(id)
            .map { it?.toAlbum() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return queries.getSongList(false, id).map { it.toSong() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return queries.observeSongList(false, id)
            .mapListItem { it.toSong() }
    }

    override fun observeRecentlyPlayed(): Flow<List<Album>> {
        return lastPlayedDao.observeAll()
            .mapListItem { it.toAlbum() }
    }

    override suspend fun addRecentlyPlayed(id: Long) {
        lastPlayedDao.insertOne(LastPlayedAlbumEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return queries.observeRecentlyAdded(false)
            .mapListItem { it.toAlbum() }
    }

    override fun observeSiblings(id: Long): Flow<List<Album>> {
        return queries.observeSiblings(id)
            .mapListItem { it.toAlbum() }
    }

    override fun observeArtistsAlbums(artistId: Long): Flow<List<Album>> {
        return observeAll()
            .filterListItem { it.artistId == artistId }
    }
}
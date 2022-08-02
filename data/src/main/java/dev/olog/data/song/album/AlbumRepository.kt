package dev.olog.data.song.album

import dev.olog.core.entity.sort.AlbumSongsSort
import dev.olog.core.entity.sort.AllAlbumsSort
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.data.mediastore.song.album.toDomain
import dev.olog.data.mediastore.song.toDomain
import dev.olog.data.sort.SortRepository
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlbumRepository @Inject constructor(
    private val albumDao: AlbumDao,
    private val sortRepository: SortRepository,
) : AlbumGateway {

    override fun getAll(): List<Album> {
        return albumDao.getAll().map { it.toDomain() }
    }

    override fun observeAll(): Flow<List<Album>> {
        return albumDao.observeAll()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun getByParam(id: Long): Album? {
        return albumDao.getById(id.toString())?.toDomain()
    }

    override fun observeByParam(id: Long): Flow<Album?> {
        return albumDao.observeById(id.toString())
            .distinctUntilChanged()
            .map { it?.toDomain() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return albumDao.getTracksById(id.toString())
            .map { it.toDomain() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return albumDao.observeTracksById(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeLastPlayed(): Flow<List<Album>> {
        return albumDao.observeLastPlayed()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override suspend fun addLastPlayed(id: Long) {
        albumDao.insertLastPlayed(LastPlayedAlbumEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return albumDao.observeRecentlyAdded()
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeSiblings(id: Long): Flow<List<Album>> {
        return albumDao.observeSiblings(id.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun observeArtistsAlbums(artistId: Long): Flow<List<Album>> {
        return albumDao.observeArtistAlbums(artistId.toString())
            .distinctUntilChanged()
            .mapListItem { it.toDomain() }
    }

    override fun setSort(sort: AllAlbumsSort) {
        sortRepository.setAllAlbumsSort(sort)
    }

    override fun getSort(): AllAlbumsSort {
        return sortRepository.getAllAlbumsSort()
    }

    override fun setSongSort(sort: AlbumSongsSort) {
        sortRepository.setAlbumSongsSort(sort)
    }

    override fun getSongSort(): AlbumSongsSort {
        return sortRepository.getAlbumSongsSort()
    }
}
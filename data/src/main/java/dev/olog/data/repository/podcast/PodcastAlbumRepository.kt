package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.data.db.dao.LastPlayedPodcastAlbumDao
import dev.olog.data.db.entities.LastPlayedPodcastAlbumEntity
import dev.olog.data.mediastore.album.toAlbum
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.AlbumsQueries
import dev.olog.shared.filterListItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PodcastAlbumRepository @Inject constructor(
    private val queries: AlbumsQueries,
    private val lastPlayedDao: LastPlayedPodcastAlbumDao,
) : PodcastAlbumGateway {

    override fun getAll(): List<Album> {
        return queries.getAll(true).map { it.toAlbum() }
    }

    override fun observeAll(): Flow<List<Album>> {
        return queries.observeAll(true)
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
        return queries.getSongList(true, id).map { it.toSong() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return queries.observeSongList(true, id)
            .mapListItem { it.toSong() }
    }

    override fun observeRecentlyPlayed(): Flow<List<Album>> {
        return lastPlayedDao.observeAll()
            .mapListItem { it.toAlbum() }
    }

    override suspend fun addRecentlyPlayed(id: Long) {
        lastPlayedDao.insertOne(LastPlayedPodcastAlbumEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Album>> {
        return queries.observeRecentlyAdded(true)
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
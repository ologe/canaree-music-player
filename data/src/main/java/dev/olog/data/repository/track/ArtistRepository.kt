package dev.olog.data.repository.track

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.data.db.dao.LastPlayedArtistDao
import dev.olog.data.db.entities.LastPlayedArtistEntity
import dev.olog.data.mediastore.artist.toArtist
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.ArtistQueries
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ArtistRepository @Inject constructor(
    private val queries: ArtistQueries,
    private val lastPlayedDao: LastPlayedArtistDao,
) : ArtistGateway {

    override fun getAll(): List<Artist> {
        return queries.getAll(false).map { it.toArtist() }
    }

    override fun observeAll(): Flow<List<Artist>> {
        return queries.observeAll(false)
            .mapListItem { it.toArtist() }
    }

    override fun getById(id: Long): Artist? {
        return queries.getById(id)?.toArtist()
    }

    override fun observeById(id: Long): Flow<Artist?> {
        return queries.observeById(id)
            .map { it?.toArtist() }
    }

    override fun getTrackListByParam(id: Long): List<Song> {
        return queries.getSongList(false, id).map { it.toSong() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return queries.observeSongList(false, id)
            .mapListItem { it.toSong() }
    }

    override fun observeRecentlyPlayed(): Flow<List<Artist>> {
        return lastPlayedDao.observeAll()
            .mapListItem { it.toArtist() }
    }

    override suspend fun addRecentlyPlayed(id: Long) {
        lastPlayedDao.insertOne(LastPlayedArtistEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return queries.observeRecentlyAdded(false)
            .mapListItem { it.toArtist() }
    }
}
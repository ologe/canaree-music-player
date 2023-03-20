package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.data.db.dao.LastPlayedPodcastArtistDao
import dev.olog.data.db.entities.LastPlayedPodcastArtistEntity
import dev.olog.data.mediastore.artist.toArtist
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.ArtistQueries
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PodcastArtistRepository @Inject constructor(
    private val queries: ArtistQueries,
    private val lastPlayedDao: LastPlayedPodcastArtistDao,
) : PodcastArtistGateway {

    override fun getAll(): List<Artist> {
        return queries.getAll(true).map { it.toArtist() }
    }

    override fun observeAll(): Flow<List<Artist>> {
        return queries.observeAll(true)
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
        return queries.getSongList(true, id).map { it.toSong() }
    }

    override fun observeTrackListByParam(id: Long): Flow<List<Song>> {
        return queries.observeSongList(true, id)
            .mapListItem { it.toSong() }
    }

    override fun observeRecentlyPlayed(): Flow<List<Artist>> {
        return lastPlayedDao.observeAll()
            .mapListItem { it.toArtist() }
    }

    override suspend fun addRecentlyPlayed(id: Long) {
        lastPlayedDao.insertOne(LastPlayedPodcastArtistEntity(id))
    }

    override fun observeRecentlyAdded(): Flow<List<Artist>> {
        return queries.observeRecentlyAdded(true)
            .mapListItem { it.toArtist() }
    }

}
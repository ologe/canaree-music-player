package dev.olog.data.repository.podcast

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.db.dao.PodcastPositionDao
import dev.olog.data.db.entities.PodcastPositionEntity
import dev.olog.data.mediastore.audio.toSong
import dev.olog.data.queries.AudioQueries
import dev.olog.shared.assertBackgroundThread
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    private val queries: AudioQueries,
    private val podcastPositionDao: PodcastPositionDao,
) : PodcastGateway {

    override fun getAll(): List<Song> {
        return queries.getAll(true).map { it.toSong() }
    }

    override fun observeAll(): Flow<List<Song>> {
        return queries.observeAll(true)
            .mapListItem { it.toSong() }
    }

    override fun getById(id: Long): Song? {
        return queries.getById(id)?.toSong()
    }

    override fun observeById(id: Long): Flow<Song?> {
        return queries.observeById(id).map { it?.toSong() }
    }

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        // TODO replace with bookmark column?
        assertBackgroundThread()
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        // TODO replace with bookmark column?
        assertBackgroundThread()
        podcastPositionDao.setPosition(PodcastPositionEntity(podcastId, position))
    }

    override fun getByAlbumId(albumId: Long): Song? {
        return queries.getByAlbumId(albumId)?.toSong()
    }
}
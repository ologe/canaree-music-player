package dev.olog.data.repository.podcast

import dev.olog.core.entity.PodcastPosition
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.db.PodcastPositionDao
import dev.olog.data.model.db.PodcastPositionEntity
import dev.olog.shared.android.utils.assertBackgroundThread
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PodcastRepository @Inject constructor(
    private val podcastPositionDao: PodcastPositionDao
) : PodcastGateway {

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        val position = podcastPositionDao.getPosition(podcastId) ?: 0L
        if (position > duration - 1000 * 5) {
            // if last 5 sec, restart
            return 0L
        }
        return position
    }

    override fun observeAllCurrentPositions(): Flow<List<PodcastPosition>> {
        return podcastPositionDao.getAllPositions()
            .mapListItem { PodcastPosition(it.id, it.position) }
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {
        assertBackgroundThread()
        podcastPositionDao.setPosition(
            PodcastPositionEntity(podcastId, position)
        )
    }

}
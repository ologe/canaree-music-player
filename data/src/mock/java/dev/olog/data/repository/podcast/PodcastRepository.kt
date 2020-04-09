package dev.olog.data.repository.podcast

import dev.olog.core.entity.PodcastPosition
import dev.olog.core.gateway.podcast.PodcastGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class PodcastRepository @Inject constructor(

) : PodcastGateway {

    override fun getCurrentPosition(podcastId: Long, duration: Long): Long {
        return 0
    }

    override fun observeAllCurrentPositions(): Flow<List<PodcastPosition>> {
        return flowOf(listOf(PodcastPosition(0, 0)))
    }

    override fun saveCurrentPosition(podcastId: Long, position: Long) {

    }
}
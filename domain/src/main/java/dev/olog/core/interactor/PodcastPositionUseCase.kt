package dev.olog.core.interactor

import dev.olog.core.gateway.podcast.PodcastGateway
import javax.inject.Inject
import kotlin.time.Duration

class PodcastPositionUseCase @Inject constructor(
        private val gateway: PodcastGateway
) {

    suspend fun get(podcastId: Long, duration: Duration): Duration {
        return gateway.getCurrentPosition(podcastId, duration)
    }

    suspend fun set(podcastId: Long, position: Duration){
        gateway.saveCurrentPosition(podcastId, position)
    }

}
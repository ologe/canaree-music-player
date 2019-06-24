package dev.olog.msc.domain.interactor

import dev.olog.core.gateway.PodcastGateway2
import javax.inject.Inject

class PodcastPositionUseCase @Inject constructor(
        private val gateway: PodcastGateway2
) {

    fun get(podcastId: Long, duration: Long): Long{
        return gateway.getCurrentPosition(podcastId, duration)
    }

    fun set(podcastId: Long, position: Long){
        gateway.saveCurrentPosition(podcastId, position)
    }

}
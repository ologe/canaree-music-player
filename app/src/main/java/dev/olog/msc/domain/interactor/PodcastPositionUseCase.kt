package dev.olog.msc.domain.interactor

import dev.olog.msc.domain.gateway.PodcastGateway
import javax.inject.Inject

class PodcastPositionUseCase @Inject constructor(
        private val gateway: PodcastGateway
) {

    fun get(podcastId: Long, duration: Long): Long{
        return gateway.getCurrentPosition(podcastId, duration)
    }

    fun set(podcastId: Long, position: Long){
        gateway.saveCurrentPosition(podcastId, position)
    }

}
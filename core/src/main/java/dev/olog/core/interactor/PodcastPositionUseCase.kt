package dev.olog.core.interactor

import dev.olog.core.MediaUri
import dev.olog.core.track.TrackGateway
import javax.inject.Inject

class PodcastPositionUseCase @Inject constructor(
    private val gateway: TrackGateway,
) {

    fun get(uri: MediaUri, duration: Long): Long {
        return gateway.getPodcastCurrentPosition(uri, duration)
    }

    fun set(uri: MediaUri, position: Long) {
        gateway.savePodcastCurrentPosition(uri, position)
    }

}
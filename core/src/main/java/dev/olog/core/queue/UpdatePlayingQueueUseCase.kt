package dev.olog.core.queue

import dev.olog.core.MediaUri
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
    private val gateway: PlayingQueueGateway
) {

    suspend operator fun invoke(param: List<Request>) {
        gateway.update(param)
    }

    data class Request(
        val uri: MediaUri,
        val playOrder: Int
    )

}
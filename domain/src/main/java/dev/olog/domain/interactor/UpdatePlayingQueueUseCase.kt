package dev.olog.domain.interactor

import dev.olog.domain.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
    private val gateway: PlayingQueueGateway
) {

    suspend operator fun invoke(param: List<Request>) {
        gateway.update(param)
    }

    data class Request(
        val songId: Long,
        val serviceProgressive: Int
    )

}
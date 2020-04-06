package dev.olog.domain.interactor

import dev.olog.domain.MediaId
import dev.olog.domain.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
    private val gateway: PlayingQueueGateway
) {

    operator fun invoke(param: List<Request>) {
        gateway.update(param)
    }

    data class Request(
        val mediaId: MediaId,
        val songId: Long,
        val idInPlaylist: Int
    )

}
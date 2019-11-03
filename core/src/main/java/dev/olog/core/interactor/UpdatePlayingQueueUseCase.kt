package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdatePlayingQueueUseCase @Inject constructor(
    private val gateway: PlayingQueueGateway
) {

    operator fun invoke(param: List<UpdatePlayingQueueUseCaseRequest>) {
        gateway.update(param)
    }

}

data class UpdatePlayingQueueUseCaseRequest(
    val mediaId: MediaId,
    val songId: Long,
    val idInPlaylist: Int
)
package dev.olog.domain.interactor.music_service

import dev.olog.domain.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdateMiniQueueUseCase @Inject constructor(
        private val gateway: PlayingQueueGateway
) {

    fun execute(param: List<Long>) {
        gateway.updateMiniQueue(param)
    }
}
package dev.olog.domain.interactor.music_service

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdateMiniQueueUseCase @Inject constructor(
        private val gateway: PlayingQueueGateway
) {

    fun execute(param: List<PlayingQueueSong>) {
        gateway.updateMiniQueue(param)
    }
}
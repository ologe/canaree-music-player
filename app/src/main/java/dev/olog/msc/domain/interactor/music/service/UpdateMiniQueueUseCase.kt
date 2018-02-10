package dev.olog.msc.domain.interactor.music.service

import dev.olog.msc.domain.entity.PlayingQueueSong
import dev.olog.msc.domain.gateway.PlayingQueueGateway
import javax.inject.Inject

class UpdateMiniQueueUseCase @Inject constructor(
        private val gateway: PlayingQueueGateway
) {

    fun execute(param: List<PlayingQueueSong>) {
        gateway.updateMiniQueue(param)
    }
}
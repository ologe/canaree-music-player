package dev.olog.domain.gateway

import dev.olog.domain.entity.PlayingQueueSong
import dev.olog.domain.interactor.UpdatePlayingQueueUseCase
import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Flow<List<PlayingQueueSong>>

    fun getAll(): List<PlayingQueueSong>

    fun update(list: List<UpdatePlayingQueueUseCase.Request>)

}
package dev.olog.core.gateway

import dev.olog.core.entity.PlayingQueueTrack
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Flow<List<PlayingQueueTrack>>

    suspend fun getAll(): List<PlayingQueueTrack>

    suspend fun update(list: List<UpdatePlayingQueueUseCaseRequest>)

}
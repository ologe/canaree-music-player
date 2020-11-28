package dev.olog.core.gateway

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.interactor.UpdatePlayingQueueUseCaseRequest
import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Flow<List<PlayingQueueSong>>

    suspend fun getAll(): List<PlayingQueueSong>

    suspend fun update(list: List<UpdatePlayingQueueUseCaseRequest>)

}
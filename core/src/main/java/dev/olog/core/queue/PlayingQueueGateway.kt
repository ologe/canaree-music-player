package dev.olog.core.queue

import kotlinx.coroutines.flow.Flow

interface PlayingQueueGateway {

    companion object {
        const val MINI_QUEUE_SIZE = 50
    }

    fun observeAll(): Flow<List<PlayingQueueSong>>

    fun getAll(): List<PlayingQueueSong>

    suspend fun update(list: List<UpdatePlayingQueueUseCase.Request>)

}
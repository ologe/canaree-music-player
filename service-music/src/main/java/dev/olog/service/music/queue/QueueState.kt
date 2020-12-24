package dev.olog.service.music.queue

import dev.olog.service.music.model.MediaEntity

internal sealed class QueueState {

    object NotSet : QueueState()

    object Empty : QueueState()

    data class Set(
        val position: Int,
        val queue: List<MediaEntity>
    ) : QueueState() {

        init {
            require(queue.isNotEmpty()) { "queue shall not be empty" }
            require(position in 0..queue.lastIndex) {
                "index should be in 0..${queue.lastIndex}, but is $position"
            }
        }

        val entity: MediaEntity
            get() = queue[position]

    }

    suspend fun <T> whenIsSet(block: suspend Set.() -> T): T? {
        if (this is Set) {
            return block(this)
        }
        return null
    }

}
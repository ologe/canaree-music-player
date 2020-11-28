package dev.olog.service.music.queue

import dev.olog.service.music.model.MediaEntity

internal sealed class QueueState {

    object NotSet : QueueState()
    data class Set(
        val items: List<MediaEntity>
    ) : QueueState()

}
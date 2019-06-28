package dev.olog.service.music.model

data class MediaSessionQueueModel<T>(
        val activeId: Long,
        val queue: List<T>
)
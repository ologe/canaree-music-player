package dev.olog.service.music.queue

import dev.olog.service.music.model.MediaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class QueueNew @Inject constructor(

) {

    private val queuePublisher: MutableStateFlow<QueueState> = MutableStateFlow(QueueState.NotSet)

    val isValidQueue: Boolean
        get() = queuePublisher.value is QueueState.Set

    fun updateQueue(items: List<MediaEntity>) {
        queuePublisher.value = QueueState.Set(items)
    }

}
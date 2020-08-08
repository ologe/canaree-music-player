package dev.olog.feature.presentation.base.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.feature.presentation.base.model.PresentationId
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SharedViewModel @ViewModelInject constructor(

) : ViewModel() {

    private val currentPlayingPublisher = ConflatedBroadcastChannel<PresentationId.Track>()

    override fun onCleared() {
        super.onCleared()
        currentPlayingPublisher.close()
    }

    fun setCurrentPlaying(mediaId: PresentationId.Track) {
        currentPlayingPublisher.offer(mediaId)
    }

    val observeCurrentPlaying: Flow<PresentationId.Track> = currentPlayingPublisher.asFlow()

}
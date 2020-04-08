package dev.olog.presentation.main

import androidx.lifecycle.ViewModel
import dev.olog.presentation.PresentationId
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class SharedViewModel @Inject constructor(

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
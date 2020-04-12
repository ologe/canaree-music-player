package dev.olog.feature.edit.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.edit.model.DisplayableArtist
import dev.olog.feature.presentation.base.model.PresentationId
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditArtistFragmentViewModel @Inject constructor(
    private val presenter: EditArtistFragmentPresenter,
    private val schedulers: Schedulers

) : ViewModel() {

    private val displayableArtistPublisher = ConflatedBroadcastChannel<DisplayableArtist>()

    fun requestData(mediaId: PresentationId.Category) = viewModelScope.launch {
        val artist = withContext(schedulers.io) {
            presenter.getArtist(mediaId)
        }
        displayableArtistPublisher.offer(artist.toDisplayableArtist())
    }

    override fun onCleared() {
        super.onCleared()
        displayableArtistPublisher.close()
    }

    fun observeData(): Flow<DisplayableArtist> = displayableArtistPublisher.asFlow()

    private fun Artist.toDisplayableArtist(): DisplayableArtist {
        return DisplayableArtist(
            id = this.id,
            title = this.name,
            albumArtist = this.albumArtist,
            songs = this.songs,
            isPodcast = this.isPodcast
        )
    }


}
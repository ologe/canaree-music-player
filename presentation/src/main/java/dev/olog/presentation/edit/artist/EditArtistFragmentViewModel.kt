package dev.olog.presentation.edit.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.schedulers.Schedulers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

class EditArtistFragmentViewModel @Inject constructor(
    private val presenter: EditArtistFragmentPresenter,
    private val schedulers: Schedulers

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private val displayableArtistLiveData = ConflatedBroadcastChannel<DisplayableArtist>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val artist = withContext(schedulers.io) {
            presenter.getArtist(mediaId)
        }
        displayableArtistLiveData.offer(artist.toDisplayableArtist())
    }

    override fun onCleared() {
        super.onCleared()
        displayableArtistLiveData.close()
    }

    fun observeData(): Flow<DisplayableArtist> = displayableArtistLiveData.asFlow()

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
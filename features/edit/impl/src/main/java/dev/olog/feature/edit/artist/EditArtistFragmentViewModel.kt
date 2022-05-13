package dev.olog.feature.edit.artist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.tag.TagOptionSingleton
import javax.inject.Inject

@HiltViewModel
class EditArtistFragmentViewModel @Inject constructor(
    private val presenter: EditArtistFragmentPresenter

) : ViewModel() {

    init {
        TagOptionSingleton.getInstance().isAndroid = true
    }

    private val displayableArtistLiveData = MutableLiveData<DisplayableArtist>()

    fun requestData(mediaId: MediaId) = viewModelScope.launch {
        val artist = withContext(Dispatchers.IO) {
            presenter.getArtist(mediaId)
        }
        displayableArtistLiveData.value = artist.toDisplayableArtist()
    }

    fun observeData(): LiveData<DisplayableArtist> = displayableArtistLiveData


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
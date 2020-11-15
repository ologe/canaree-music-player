package dev.olog.presentation.edit.artist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.shared.android.extensions.argument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.tag.TagOptionSingleton

class EditArtistFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val presenter: EditArtistFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(EditArtistFragment.ARGUMENTS_MEDIA_ID, MediaId::fromString)
    private val displayablePublisher = MutableStateFlow<DisplayableArtist?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val artist = withContext(Dispatchers.IO) {
                presenter.getArtist(mediaId).toDisplayableArtist()
            }
            displayablePublisher.value = artist
        }
    }

    fun observeData(): Flow<DisplayableArtist> = displayablePublisher.filterNotNull()

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
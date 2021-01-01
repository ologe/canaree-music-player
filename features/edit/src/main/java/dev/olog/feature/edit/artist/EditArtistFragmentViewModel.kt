package dev.olog.feature.edit.artist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.mediaid.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.tag.TagOptionSingleton

internal class EditArtistFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val presenter: EditArtistFragmentPresenter
) : ViewModel() {

    private val mediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)
    private val publisher = MutableStateFlow<EditArtistFragmentModel?>(null)

    init {
        TagOptionSingleton.getInstance().isAndroid = true

        viewModelScope.launch {
            val artist = withContext(Dispatchers.IO) {
                presenter.getArtist(mediaId).toDisplayableArtist()
            }
            publisher.value = artist
        }
    }

    fun observeData(): Flow<EditArtistFragmentModel> = publisher.filterNotNull()

    private fun Artist.toDisplayableArtist(): EditArtistFragmentModel {
        return EditArtistFragmentModel(
            id = this.id,
            title = this.name,
            albumArtist = this.albumArtist,
            songs = this.songs,
            isPodcast = this.isPodcast
        )
    }


}
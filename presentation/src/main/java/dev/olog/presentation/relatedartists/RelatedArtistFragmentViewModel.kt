package dev.olog.presentation.relatedartists

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.presentation.R
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase,
    handle: SavedStateHandle,
) : ViewModel() {

    private val mediaId = MediaId.fromString(handle.get(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!)

    val itemOrdinal = mediaId.category.ordinal

    private val liveData = MutableLiveData<List<RelatedArtistItem>>()
    private val titleLiveData = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            useCase(mediaId)
                .mapListItem { it.toRelatedArtist(resources) }
                .flowOn(Dispatchers.IO)
                .collect { liveData.value = it }
        }
        viewModelScope.launch {
            getItemTitleUseCase(mediaId)
                .flowOn(Dispatchers.IO)
                .collect { titleLiveData.value = it }
        }
    }

    fun observeData(): LiveData<List<RelatedArtistItem>> = liveData
    fun observeTitle(): LiveData<String> = titleLiveData

    private fun Artist.toRelatedArtist(resources: Resources): RelatedArtistItem {
        val songs = resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return RelatedArtistItem(
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}
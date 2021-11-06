package dev.olog.feature.detail.related.artist

import android.content.res.Resources
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.feature.base.model.DisplayableAlbum
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.detail.R
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    savedStateHandle: SavedStateHandle,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    private val mediaId = MediaId.fromString(savedStateHandle.get<String>(RelatedArtistFragment.ARGUMENTS_MEDIA_ID)!!)

    val itemOrdinal = mediaId.category.ordinal

    private val liveData = MutableLiveData<List<DisplayableItem>>()
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

    fun observeData(): LiveData<List<DisplayableItem>> = liveData
    fun observeTitle(): LiveData<String> = titleLiveData

    override fun onCleared() {
        viewModelScope.cancel()
    }

    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs =
            resources.getQuantityString(localization.R.plurals.common_plurals_song, this.songs, this.songs)

        return DisplayableAlbum(
            type = R.layout.item_related_artist,
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}
package dev.olog.presentation.relatedartists

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRelatedArtistsUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class RelatedArtistFragmentViewModel @Inject constructor(
    resources: Resources,
    mediaId: MediaId,
    useCase: ObserveRelatedArtistsUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    private val liveData = MutableLiveData<List<DisplayableItem>>()
    private val titleLiveData = MutableLiveData<String>()

    init {
        useCase(mediaId)
            .mapListItem { it.toRelatedArtist(resources) }
            .flowOn(Dispatchers.IO)
            .onEach { liveData.value = it }
            .launchIn(viewModelScope)

        getItemTitleUseCase(mediaId)
            .flowOn(Dispatchers.IO)
            .map { it ?: "" }
            .onEach { titleLiveData.value = it }
            .launchIn(viewModelScope)
    }

    fun observeData(): LiveData<List<DisplayableItem>> = liveData
    fun observeTitle(): LiveData<String> = titleLiveData


    private fun Artist.toRelatedArtist(resources: Resources): DisplayableItem {
        val songs =
            resources.getQuantityString(R.plurals.common_plurals_song, this.songs, this.songs)

        return DisplayableAlbum(
            type = R.layout.item_related_artist,
            mediaId = getMediaId(),
            title = this.name,
            subtitle = songs
        )
    }

}
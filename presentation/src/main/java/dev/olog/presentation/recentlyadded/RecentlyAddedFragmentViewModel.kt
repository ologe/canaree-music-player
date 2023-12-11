package dev.olog.presentation.recentlyadded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentlyAddedFragmentViewModel @Inject constructor(
    mediaId: MediaId,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    val itemOrdinal = mediaId.category.ordinal

    private val liveData = MutableLiveData<List<RecentlyAddedItem>>()
    private val titleLiveData = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            useCase(mediaId)
                .mapListItem { it.toRecentDetailDisplayableItem(mediaId) }
                .flowOn(Dispatchers.IO)
                .collect { liveData.value = it }
        }
        viewModelScope.launch {
            getItemTitleUseCase(mediaId)
                .flowOn(Dispatchers.IO)
                .collect { titleLiveData.value = it }
        }
    }

    fun observeData(): LiveData<List<RecentlyAddedItem>> = liveData
    fun observeTitle(): LiveData<String> = titleLiveData

    private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): RecentlyAddedItem {
        return RecentlyAddedItem(
            mediaId = MediaId.playableItem(parentId, id),
            title = title,
            subtitle = DisplayableTrack.subtitle(artist, album),
        )
    }


}

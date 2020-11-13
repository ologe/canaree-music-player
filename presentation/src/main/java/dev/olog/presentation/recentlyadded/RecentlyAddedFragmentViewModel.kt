package dev.olog.presentation.recentlyadded

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.GetItemTitleUseCase
import dev.olog.core.interactor.ObserveRecentlyAddedUseCase
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.recentlyadded.RecentlyAddedFragment.Companion.ARGUMENTS_MEDIA_ID
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

internal class RecentlyAddedFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    useCase: ObserveRecentlyAddedUseCase,
    getItemTitleUseCase: GetItemTitleUseCase

) : ViewModel() {

    private val mediaId = state.argument(ARGUMENTS_MEDIA_ID, initializer = MediaId::fromString)

    val itemOrdinal = mediaId.category.ordinal

    private val liveData = MutableLiveData<List<DisplayableItem>>()
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

    fun observeData(): LiveData<List<DisplayableItem>> = liveData
    fun observeTitle(): LiveData<String> = titleLiveData

    override fun onCleared() {
        viewModelScope.cancel()
    }

    private fun Song.toRecentDetailDisplayableItem(parentId: MediaId): DisplayableItem {
        return DisplayableTrack(
            type = R.layout.item_recently_added,
            mediaId = MediaId.playableItem(parentId, id),
            title = title,
            artist = artist,
            album = album,
            idInPlaylist = idInPlaylist,
            dataModified = this.dateModified
        )
    }


}

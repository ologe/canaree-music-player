package dev.olog.presentation.createplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.presentation.createplaylist.mapper.toDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistFragmentViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val getAllSongsUseCase: SongGateway,
    private val getAllPodcastsUseCase: PodcastGateway,
) : ViewModel() {

    private val isPodcast = handle.get<Boolean>(CreatePlaylistFragment.ARGUMENT_IS_PODCAST)!!
    private val data = MutableLiveData<List<DisplayableItem>>()

    private val _selectedIds = mutableListOf<Long>()
    val selectedIds: List<Long>
        get() = _selectedIds.toList()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private val showOnlyFiltered = ConflatedBroadcastChannel(false)

    private val filterChannel = ConflatedBroadcastChannel("")

    init {
        viewModelScope.launch {
            showOnlyFiltered.asFlow()
                .flatMapLatest { onlyFiltered ->
                    if (onlyFiltered){
                        getPlaylistTypeTracks().map { songs -> songs.filter { _selectedIds.contains(it.id) } }
                    } else {
                        getPlaylistTypeTracks().combine(filterChannel.asFlow()) { tracks, filter ->
                            if (filter.isNotEmpty()) {
                                tracks.filter {
                                    it.title.contains(filter, true) ||
                                            it.artist.contains(filter, true) ||
                                            it.album.contains(filter, true)
                                }
                            } else {
                                tracks
                            }
                        }
                    }
                }.mapListItem { it.toDisplayableItem() }
                .flowOn(Dispatchers.Default)
                .collect { data.value = it }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }

    fun updateFilter(filter: String) {
        filterChannel.trySend(filter)
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data

    private fun getPlaylistTypeTracks(): Flow<List<Song>> {
        if (isPodcast) {
            return getAllPodcastsUseCase.observeAll()
        }
        return getAllSongsUseCase.observeAll()
    }

    fun toggleItem(mediaId: MediaId) {
        val id = mediaId.id
        if (_selectedIds.contains(id)) {
            _selectedIds.remove(id)
        } else {
            _selectedIds.add(id)
        }
        selectionCountLiveData.postValue(_selectedIds.size)
    }

    fun toggleShowOnlyFiltered() {
        val onlyFiltered = showOnlyFiltered.value
        showOnlyFiltered.trySend(!onlyFiltered)
    }

    fun isChecked(mediaId: MediaId): Boolean {
        return _selectedIds.contains(mediaId.id)
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

}
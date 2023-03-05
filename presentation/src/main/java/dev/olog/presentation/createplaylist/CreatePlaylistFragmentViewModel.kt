package dev.olog.presentation.createplaylist

import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.presentation.createplaylist.mapper.toDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import dev.olog.shared.android.extensions.toList
import dev.olog.shared.android.extensions.toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistFragmentViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val getAllSongsUseCase: SongGateway,
    private val getAllPodcastsUseCase: PodcastGateway,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val playlistType = PlaylistType.values()[handle.get(CreatePlaylistFragment.ARGUMENT_PLAYLIST_TYPE)!!]
    private val data = MutableLiveData<List<DisplayableItem>>()

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private val showOnlyFiltered = ConflatedBroadcastChannel(false)

    private val filterChannel = ConflatedBroadcastChannel("")

    init {
        viewModelScope.launch {
            showOnlyFiltered.asFlow()
                .flatMapLatest { onlyFiltered ->
                    if (onlyFiltered){
                        getPlaylistTypeTracks().map { songs -> songs.filter { selectedIds.contains(it.id) } }
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
        filterChannel.offer(filter)
    }

    fun observeData(): LiveData<List<DisplayableItem>> = data

    private fun getPlaylistTypeTracks(): Flow<List<Song>> = when (playlistType) {
        PlaylistType.PODCAST -> getAllPodcastsUseCase.observeAll()
        PlaylistType.TRACK -> getAllSongsUseCase.observeAll()
        PlaylistType.AUTO -> throw IllegalArgumentException("type auto not valid")
    }

    fun toggleItem(mediaId: MediaId) {
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountLiveData.postValue(selectedIds.size())
    }

    fun toggleShowOnlyFiltered() {
        val onlyFiltered = showOnlyFiltered.value
        showOnlyFiltered.offer(!onlyFiltered)
    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

    suspend fun savePlaylist(playlistTitle: String): Boolean {
        if (selectedIds.isEmpty()) {
            throw IllegalStateException("not supposed to happen, save button must be invisible")
        }
        withContext(Dispatchers.IO){
            insertCustomTrackListToPlaylist(
                InsertCustomTrackListRequest(
                    playlistTitle,
                    selectedIds.toList(),
                    playlistType
                )
            )
        }

        return true
    }

}
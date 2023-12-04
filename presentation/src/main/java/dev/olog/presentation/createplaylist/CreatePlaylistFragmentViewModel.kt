package dev.olog.presentation.createplaylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.sort.SortType
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.presentation.createplaylist.mapper.CreatePlaylistFragmentItem
import dev.olog.presentation.createplaylist.mapper.toDisplayableItem
import androidx.lifecycle.asLiveData
import dev.olog.shared.android.extensions.map
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistFragmentViewModel @Inject constructor(
    private val playlistType: PlaylistType,
    private val getAllSongsUseCase: SongGateway,
    private val getAllPodcastsUseCase: PodcastGateway,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist
) : ViewModel() {

    private val data = MutableStateFlow<List<CreatePlaylistFragmentItem>?>(null)
    private val showOnlySelected = MutableStateFlow(false)
    private val filterChannel = MutableStateFlow("")

    init {
        viewModelScope.launch {
            getPlaylistTypeTracks()
                .take(1)
                .mapListItem { it.toDisplayableItem() }
                .flowOn(Dispatchers.Default)
                .collect { data.value = it }
        }
    }

    fun updateFilter(filter: String) {
        filterChannel.value = filter
    }

    fun observeData(): LiveData<List<CreatePlaylistFragmentItem>> {
        return combine(
            data.filterNotNull(),
            showOnlySelected,
            filterChannel
        ) { list, onlySelected, filter ->
            if (onlySelected) {
                list.filter { it.isChecked }
            } else if (filter.isBlank()) {
                list
            } else {
                list.filter {
                    it.title.contains(filter, true) ||
                        it.artist.contains(filter, true) ||
                        it.album.contains(filter, true)
                }
            }
        }.asLiveData()
    }

    fun observeLetters(): LiveData<List<String>> {
        return observeData().map {
            it.asSequence()
                .mapNotNull { it.getText(SortType.TITLE).firstOrNull()?.uppercase() }
                .distinct()
                .toList()
        }
    }

    private fun getPlaylistTypeTracks(): Flow<List<Song>> = when (playlistType) {
        PlaylistType.PODCAST -> getAllPodcastsUseCase.observeAll()
        PlaylistType.TRACK -> getAllSongsUseCase.observeAll()
        PlaylistType.AUTO -> throw IllegalArgumentException("type auto not valid")
    }

    fun toggleItem(mediaId: MediaId) {
        data.update { list ->
            list.orEmpty().map { item ->
                if (item.mediaId == mediaId) {
                    item.copy(isChecked = !item.isChecked)
                } else {
                    item
                }
            }
        }
    }

    fun toggleShowOnlyFiltered() {
        showOnlySelected.value = !showOnlySelected.value
    }

    fun observeSelectedCount(): LiveData<Int> {
        return data.filterNotNull()
            .map { list ->
                list.filter { it.isChecked }.size
            }.asLiveData()
    }

    suspend fun savePlaylist(playlistTitle: String): Boolean {
        val selectedIds = data.value.orEmpty()
            .filter { it.isChecked }
            .map { it.mediaId.resolveId }

        withContext(Dispatchers.IO) {

            insertCustomTrackListToPlaylist(
                InsertCustomTrackListRequest(
                    playlistTitle = playlistTitle,
                    tracksId = selectedIds,
                    type = playlistType
                )
            )
        }

        return true
    }

}
package dev.olog.feature.edit.playlist.create

import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.playlist.InsertCustomTrackListRequest
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.android.extensions.toList
import dev.olog.shared.android.extensions.toggle
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

// TODO refactor
internal class CreatePlaylistFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val getAllSongsUseCase: SongGateway,
    private val getAllPodcastsUseCase: PodcastGateway,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val playlistType = state.argument<PlaylistType>(Params.TYPE)
    private val data = MutableStateFlow<List<CreatePlaylistFragmentModel>>(emptyList())

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountPublisher = MutableStateFlow(0)
    private val showOnlyFiltered = MutableStateFlow(false)

    private val filterPublisher = MutableStateFlow("")

    init {
        showOnlyFiltered
            .flatMapLatest { onlyFiltered ->
                if (onlyFiltered){
                    getPlaylistTypeTracks().map { songs -> songs.filter { selectedIds.contains(it.id) } }
                } else {
                    getPlaylistTypeTracks().combine(filterPublisher) { tracks, filter ->
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
            .onEach { data.value = it }
            .launchIn(viewModelScope)
    }

    fun updateFilter(filter: String) {
        filterPublisher.value = filter
    }

    fun observeData(): Flow<List<CreatePlaylistFragmentModel>> = data

    private fun getPlaylistTypeTracks(): Flow<List<Track>> = when (playlistType) {
        PlaylistType.PODCAST -> getAllPodcastsUseCase.observeAll()
        PlaylistType.TRACK -> getAllSongsUseCase.observeAll()
        PlaylistType.AUTO -> throw IllegalArgumentException("type auto not valid")
    }

    fun toggleItem(mediaId: MediaId) {
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountPublisher.value = selectedIds.size()
    }

    fun toggleShowOnlyFiltered() {
        showOnlyFiltered.value = !showOnlyFiltered.value
    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): Flow<Int> = selectionCountPublisher

    suspend fun savePlaylist(playlistTitle: String): Boolean {
        if (selectedIds.isEmpty()) {
            error("not supposed to happen, save button must be invisible")
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
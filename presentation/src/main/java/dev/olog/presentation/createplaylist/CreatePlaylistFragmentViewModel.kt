package dev.olog.presentation.createplaylist

import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.domain.schedulers.Schedulers
import dev.olog.presentation.PresentationId
import dev.olog.presentation.createplaylist.mapper.toDisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.android.extensions.toList
import dev.olog.shared.android.extensions.toggle
import dev.olog.shared.mapListItem
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreatePlaylistFragmentViewModel @Inject constructor(
    private val playlistType: PlaylistType,
    private val trackGateway: TrackGateway,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val schedulers: Schedulers

) : ViewModel() {

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountPublisher = ConflatedBroadcastChannel<Int>()
    private val showOnlyFilteredPublisher = ConflatedBroadcastChannel(false)

    private val filterChannel = ConflatedBroadcastChannel("")

    val data: Flow<List<DisplayableTrack>> = showOnlyFilteredPublisher.asFlow()
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
        .flowOn(schedulers.cpu)

    override fun onCleared() {
        super.onCleared()
        filterChannel.close()
        selectionCountPublisher.close()
        showOnlyFilteredPublisher.close()

    }

    fun updateFilter(filter: String) {
        filterChannel.offer(filter)
    }

    private fun getPlaylistTypeTracks(): Flow<List<Song>> = when (playlistType) {
        PlaylistType.PODCAST -> trackGateway.observeAllPodcasts()
        PlaylistType.TRACK -> trackGateway.observeAllTracks()
    }

    fun toggleItem(mediaId: PresentationId.Track) {
        val id = mediaId.id.toLong()
        selectedIds.toggle(id, id)
        selectionCountPublisher.offer(selectedIds.size())
    }

    fun toggleShowOnlyFiltered() {
        val onlyFiltered = showOnlyFilteredPublisher.value
        showOnlyFilteredPublisher.offer(!onlyFiltered)
    }

    fun isChecked(mediaId: PresentationId.Track): Boolean {
        val id = mediaId.id.toLong()
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): Flow<Int> = selectionCountPublisher.asFlow()

    suspend fun savePlaylist(playlistTitle: String): Boolean {
        if (selectedIds.isEmpty()) {
            throw IllegalStateException("not supposed to happen, save button must be invisible")
        }
        withContext(schedulers.io){
            insertCustomTrackListToPlaylist(
                InsertCustomTrackListToPlaylist.Input(
                    playlistTitle,
                    selectedIds.toList(),
                    playlistType
                )
            )
        }

        return true
    }

}
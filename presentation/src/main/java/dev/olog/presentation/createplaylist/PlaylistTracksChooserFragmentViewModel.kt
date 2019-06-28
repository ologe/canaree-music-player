package dev.olog.presentation.createplaylist

import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.PodcastGateway
import dev.olog.core.gateway.SongGateway
import dev.olog.core.interactor.InsertCustomTrackListRequest
import dev.olog.core.interactor.InsertCustomTrackListToPlaylist
import dev.olog.presentation.createplaylist.mapper.toDisplayableItem
import dev.olog.presentation.createplaylist.mapper.toPlaylistTrack
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PlaylistTrack
import dev.olog.shared.extensions.asLiveData
import dev.olog.shared.extensions.mapToList
import dev.olog.shared.extensions.toList
import dev.olog.shared.extensions.toggle
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class PlaylistTracksChooserFragmentViewModel @Inject constructor(
    private val playlistType: PlaylistType,
    private val getAllSongsUseCase: SongGateway,
    private val getAllPodcastsUseCase: PodcastGateway,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private val showOnlyFiltered = MutableLiveData<Boolean>()

    init {
        showOnlyFiltered.postValue(false)
    }

    fun getAllSongs(filter: Observable<String>): LiveData<List<DisplayableItem>> {
        return Transformations.switchMap(showOnlyFiltered) { onlyFiltered ->
            if (onlyFiltered) {
                getPlaylistTypeTracks()
                    .map { songs -> songs.filter { selectedIds.contains(it.id) } }
            } else {
                Observables.combineLatest(
                    filter, getPlaylistTypeTracks()
                ) { query, tracks ->
                    tracks.filter {
                        it.title.contains(query, true) ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                    }
                }
            }.mapToList { it.toDisplayableItem() }
                .asLiveData()
        }
    }

    private fun getPlaylistTypeTracks(): Observable<List<PlaylistTrack>> = when (playlistType) {
        PlaylistType.PODCAST -> getAllPodcastsUseCase.observeAll().asObservable().mapToList { it.toPlaylistTrack() }
        PlaylistType.TRACK -> getAllSongsUseCase.observeAll().asObservable().mapToList { it.toPlaylistTrack() }
        PlaylistType.AUTO -> throw IllegalArgumentException("type auto not valid")
    }.map { list -> list.sortedBy { it.title.toLowerCase() } }

    fun toggleItem(mediaId: MediaId) {
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountLiveData.postValue(selectedIds.size())
    }

    fun toggleShowOnlyFiltered() {
        val onlyFiltered = showOnlyFiltered.value!!
        showOnlyFiltered.postValue(!onlyFiltered)

    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

    fun savePlaylist(playlistTitle: String): Completable {
        if (selectedIds.isEmpty()) {
            return Completable.error(IllegalStateException("empty list"))
        }
        return insertCustomTrackListToPlaylist.execute(
            InsertCustomTrackListRequest(
                playlistTitle, selectedIds.toList(), playlistType
            )
        )
    }

}
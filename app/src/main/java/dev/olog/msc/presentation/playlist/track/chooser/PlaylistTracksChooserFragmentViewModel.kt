package dev.olog.msc.presentation.playlist.track.chooser

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.util.LongSparseArray
import androidx.core.util.contains
import androidx.core.util.isEmpty
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.InsertCustomTrackListRequest
import dev.olog.msc.domain.interactor.InsertCustomTrackListToPlaylist
import dev.olog.msc.domain.interactor.tab.GetAllSongsUseCase
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.mapToList
import dev.olog.msc.utils.k.extension.toList
import dev.olog.msc.utils.k.extension.toggle
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

class PlaylistTracksChooserFragmentViewModel(
        private val getAllSongsUseCase: GetAllSongsUseCase,
        private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist

) : ViewModel() {

    private val selectedIds = LongSparseArray<Long>()
    private val selectionCountLiveData = MutableLiveData<Int>()
    private val showOnlyFiltered = MutableLiveData<Boolean>()

    init {
        showOnlyFiltered.postValue(false)
    }

    fun getAllSongs(filter: Observable<String>): LiveData<List<DisplayableItem>> {
        return Transformations.switchMap(showOnlyFiltered, { onlyFiltered ->
            if (onlyFiltered){
                getAllSongsUseCase.execute()
                        .map { it.filter { selectedIds.contains(it.id) } }
            } else {
                Observables.combineLatest(
                        filter, getAllSongsUseCase.execute(),
                        { query, tracks -> tracks.filter { it.title.contains(query, true)  ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                        } }
                )
            }.mapToList { it.toDisplayableItem() }
                    .asLiveData()
        })
    }

    fun toggleItem(mediaId: MediaId){
        val id = mediaId.resolveId
        selectedIds.toggle(id, id)
        selectionCountLiveData.postValue(selectedIds.size())
    }

    fun toggleShowOnlyFiltered(){
        val onlyFiltered = showOnlyFiltered.value!!
        showOnlyFiltered.postValue(!onlyFiltered)

    }

    fun isChecked(mediaId: MediaId): Boolean {
        val id = mediaId.resolveId
        return selectedIds[id] != null
    }

    fun observeSelectedCount(): LiveData<Int> = selectionCountLiveData

    fun savePlaylist(playlistTitle: String): Completable {
        if (selectedIds.isEmpty()){
            return Completable.error(IllegalStateException("empty list"))
        }
        return insertCustomTrackListToPlaylist.execute(InsertCustomTrackListRequest(
                playlistTitle, selectedIds.toList())
        )
    }

    private fun Song.toDisplayableItem(): DisplayableItem {
        return DisplayableItem(
                R.layout.item_choose_track,
                MediaId.songId(this.id),
                this.title,
                DisplayableItem.adjustArtist(this.artist),
                this.image,
                true,
                this.isRemix,
                this.isExplicit
        )
    }

}
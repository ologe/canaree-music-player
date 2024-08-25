package dev.olog.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.interactor.sort.GetDetailSortUseCase
import dev.olog.core.interactor.sort.SetSortOrderUseCase
import dev.olog.core.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.presentation.detail.adapter.DetailItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailFragmentViewModel @Inject constructor(
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val getSortOrderUseCase: GetDetailSortUseCase,
    private val toggleSortArrangingUseCase: ToggleDetailSortArrangingUseCase,
    handle: SavedStateHandle,
) : ViewModel() {

    val parentMediaId = MediaId.fromString(handle.get(DetailFragment.ARGUMENTS_MEDIA_ID)!!)

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val livedata = MutableLiveData<List<DetailItem>>()

    init {
        // songs
        viewModelScope.launch {
            dataProvider.observe(parentMediaId)
                .flowOn(Dispatchers.Default)
                .collect { livedata.value = it }
        }
    }

    fun observeData(): LiveData<List<DetailItem>> = livedata

    fun detailSortDataUseCase(mediaId: MediaId, action: (SortEntity) -> Unit) {
        val sortOrder = getSortOrderUseCase(mediaId)
        action(sortOrder)
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        setSortOrderUseCase(SetSortOrderUseCase.Request(parentMediaId, sortType))
    }

    fun toggleSortArranging() {
        if (parentMediaId.category == MediaIdCategory.PLAYLISTS &&
            getSortOrderUseCase(parentMediaId).type == SortType.CUSTOM){
            return
        }
        toggleSortArrangingUseCase(parentMediaId.category)
    }

    fun addMove(from: Int, to: Int){
        moveList.add(from to to)
    }

    fun processMove() = viewModelScope.launch {
        if (parentMediaId.isPlaylist || parentMediaId.isPodcastPlaylist){
            presenter.moveInPlaylist(parentMediaId, moveList)
        }
        moveList.clear()
    }

    fun removeFromPlaylist(mediaId: MediaId, idInPlaylist: Int) = viewModelScope.launch(Dispatchers.Default) {
        presenter.removeFromPlaylist(parentMediaId, mediaId, idInPlaylist)
    }

    fun showSortByTutorialIfNeverShown(): Boolean { // TODO
        return presenter.showSortByTutorialIfNeverShown()
    }

}
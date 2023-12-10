package dev.olog.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.interactor.sort.GetDetailSortUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.core.interactor.sort.SetSortOrderUseCase
import dev.olog.core.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.presentation.detail.adapter.DetailFragmentItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val getSortOrderUseCase: GetDetailSortUseCase,
    private val observeSortOrderUseCase: ObserveDetailSortUseCase,
    private val toggleSortArrangingUseCase: ToggleDetailSortArrangingUseCase,

) : ViewModel() {

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterChannel = MutableStateFlow("")

    fun updateFilter(filter: String) {
        filterChannel.value = filter
    }

    fun getFilter(): String = filterChannel.value
    fun getSort(): SortEntity = getSortOrderUseCase(mediaId)

    private val dataLiveData = MutableLiveData<List<DetailFragmentItem>>()

    init {
        // songs
        viewModelScope.launch {
            dataProvider.observe(mediaId, filterChannel)
                .flowOn(Dispatchers.Default)
                .collect { dataLiveData.value = it }
        }
    }

    fun observeSongs(): LiveData<List<DetailFragmentItem>> = dataLiveData

    fun observeSortOrder(action: (SortType) -> Unit) {
        val sortEntity = getSortOrderUseCase(mediaId)
        action(sortEntity.type)
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(Dispatchers.IO) {
        setSortOrderUseCase(SetSortOrderUseCase.Request(mediaId, sortType))
    }

    fun toggleSortArranging() {
        if (mediaId.category == MediaIdCategory.PLAYLISTS &&
            getSortOrderUseCase(mediaId).type == SortType.CUSTOM){
            return
        }
        toggleSortArrangingUseCase(mediaId.category)
    }

    fun addMove(from: Int, to: Int){
        moveList.add(from to to)
    }

    fun processMove() = viewModelScope.launch {
        if (mediaId.isPlaylist || mediaId.isPodcastPlaylist){
            presenter.moveInPlaylist(moveList)
        }
        moveList.clear()
    }

    fun removeFromPlaylist(item: DetailFragmentItem.Track.ForPlaylist) = viewModelScope.launch(Dispatchers.Default) {
        presenter.removeFromPlaylist(item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(mediaId)
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
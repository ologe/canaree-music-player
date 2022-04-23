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
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.interactor.sort.GetDetailSortUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.core.interactor.sort.SetSortOrderUseCase
import dev.olog.core.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class DetailFragmentViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val getSortOrderUseCase: GetDetailSortUseCase,
    private val observeSortOrderUseCase: ObserveDetailSortUseCase,
    private val toggleSortArrangingUseCase: ToggleDetailSortArrangingUseCase,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : ViewModel() {

    val mediaId: MediaId = handle[DetailFragment.ARGUMENTS_MEDIA_ID]!!

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val VISIBLE_RECENTLY_ADDED_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterChannel = ConflatedBroadcastChannel("")

    fun updateFilter(filter: String) {
        filterChannel.trySend(filter)
    }

    fun getFilter(): String = filterChannel.value

    private val itemLiveData = MutableLiveData<DisplayableItem>()
    private val mostPlayedLiveData = MutableLiveData<List<DisplayableTrack>>()
    private val relatedArtistsLiveData = MutableLiveData<List<DisplayableItem>>()
    private val siblingsLiveData = MutableLiveData<List<DisplayableItem>>()
    private val recentlyAddedLiveData = MutableLiveData<List<DisplayableItem>>()
    private val songLiveData = MutableLiveData<List<DisplayableItem>>()

    private val biographyLiveData = MutableLiveData<String?>()

    init {
        // header
        viewModelScope.launch {
            dataProvider.observeHeader(mediaId)
                .flowOn(Dispatchers.Default)
                .collect { itemLiveData.value = it[0] }
        }
        // most played
        viewModelScope.launch {
            dataProvider.observeMostPlayed(mediaId)
                .mapListItem { it as DisplayableTrack }
                .flowOn(Dispatchers.Default)
                .collect { mostPlayedLiveData.value = it }
        }
        // related artists
        viewModelScope.launch {
            dataProvider.observeRelatedArtists(mediaId)
                .map { it.take(RELATED_ARTISTS_TO_SEE) }
                .flowOn(Dispatchers.Default)
                .collect { relatedArtistsLiveData.value = it }
        }
        // siblings
        viewModelScope.launch {
            dataProvider.observeSiblings(mediaId)
                .flowOn(Dispatchers.Default)
                .collect { siblingsLiveData.value = it }
        }
        // recent
        viewModelScope.launch {
            dataProvider.observeRecentlyAdded(mediaId)
                .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
                .flowOn(Dispatchers.Default)
                .collect { recentlyAddedLiveData.value = it }
        }
        // songs
        viewModelScope.launch {
            dataProvider.observe(mediaId, filterChannel.asFlow())
                .flowOn(Dispatchers.Default)
                .collect { songLiveData.value = it }
        }

        // biography
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val biography = when {
                    mediaId.isArtist -> imageRetrieverGateway.getArtist(mediaId.categoryId)?.wiki
                    mediaId.isAlbum -> imageRetrieverGateway.getAlbum(mediaId.categoryId)?.wiki
                    else -> null
                }
                withContext(Dispatchers.Main) {
                    biographyLiveData.value = biography
                }
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
            }
        }
    }

    fun observeItem(): LiveData<DisplayableItem> = itemLiveData
    fun observeMostPlayed(): LiveData<List<DisplayableTrack>> = mostPlayedLiveData
    fun observeRecentlyAdded(): LiveData<List<DisplayableItem>> = recentlyAddedLiveData
    fun observeRelatedArtists(): LiveData<List<DisplayableItem>> = relatedArtistsLiveData
    fun observeSiblings(): LiveData<List<DisplayableItem>> = siblingsLiveData
    fun observeSongs(): LiveData<List<DisplayableItem>> = songLiveData
    fun observeBiography(): LiveData<String?> = biographyLiveData

    fun detailSortDataUseCase(mediaId: MediaId, action: (SortEntity) -> Unit) {
        val sortOrder = getSortOrderUseCase(mediaId)
        action(sortOrder)
    }

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
            presenter.moveInPlaylist(mediaId, moveList)
        }
        moveList.clear()
    }

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(Dispatchers.Default) {
        require(item is DisplayableTrack)
        presenter.removeFromPlaylist(mediaId, item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(mediaId)
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
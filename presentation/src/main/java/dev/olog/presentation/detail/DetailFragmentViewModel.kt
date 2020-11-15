package dev.olog.presentation.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.interactor.sort.GetDetailSortUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.core.interactor.sort.SetSortOrderUseCase
import dev.olog.core.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.presentation.detail.DetailFragment.Companion.ARGUMENTS_MEDIA_ID
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.android.extensions.argument
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DetailFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val getSortOrderUseCase: GetDetailSortUseCase,
    private val observeSortOrderUseCase: ObserveDetailSortUseCase,
    private val toggleSortArrangingUseCase: ToggleDetailSortArrangingUseCase,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val VISIBLE_RECENTLY_ADDED_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    val parentMediaId = state.argument(ARGUMENTS_MEDIA_ID, initializer = MediaId::fromString)

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterPublisher = MutableStateFlow("")

    fun updateFilter(filter: String) {
        filterPublisher.value = filter
    }

    fun getFilter(): String = filterPublisher.value

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
            dataProvider.observeHeader(parentMediaId)
                .flowOn(Dispatchers.Default)
                .collect { itemLiveData.value = it[0] }
        }
        // most played
        viewModelScope.launch {
            dataProvider.observeMostPlayed(parentMediaId)
                .mapListItem { it as DisplayableTrack }
                .flowOn(Dispatchers.Default)
                .collect { mostPlayedLiveData.value = it }
        }
        // related artists
        viewModelScope.launch {
            dataProvider.observeRelatedArtists(parentMediaId)
                .map { it.take(RELATED_ARTISTS_TO_SEE) }
                .flowOn(Dispatchers.Default)
                .collect { relatedArtistsLiveData.value = it }
        }
        // siblings
        viewModelScope.launch {
            dataProvider.observeSiblings(parentMediaId)
                .flowOn(Dispatchers.Default)
                .collect { siblingsLiveData.value = it }
        }
        // recent
        viewModelScope.launch {
            dataProvider.observeRecentlyAdded(parentMediaId)
                .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
                .flowOn(Dispatchers.Default)
                .collect { recentlyAddedLiveData.value = it }
        }
        // songs
        viewModelScope.launch {
            dataProvider.observe(parentMediaId, filterPublisher)
                .flowOn(Dispatchers.Default)
                .collect { songLiveData.value = it }
        }

        // biography
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val biography = when {
                    parentMediaId.isArtist -> imageRetrieverGateway.getArtist(parentMediaId.categoryId)?.wiki
                    parentMediaId.isAlbum -> imageRetrieverGateway.getAlbum(parentMediaId.categoryId)?.wiki
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
        val sortEntity = getSortOrderUseCase(parentMediaId)
        action(sortEntity.type)
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

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(Dispatchers.Default) {
        require(item is DisplayableTrack)
        presenter.removeFromPlaylist(parentMediaId, item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(parentMediaId)
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
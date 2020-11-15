package dev.olog.presentation.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    dataProvider: DetailDataProvider,
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

    val parentMediaId = state.argument(ARGUMENTS_MEDIA_ID, MediaId::fromString)

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterPublisher = MutableStateFlow("")

    fun updateFilter(filter: String) {
        filterPublisher.value = filter
    }

    fun getFilter(): String = filterPublisher.value

    private val itemPublisher = MutableStateFlow<DisplayableItem?>(null)
    private val mostPlayedPublisher = MutableStateFlow<List<DisplayableTrack>>(emptyList())
    private val relatedArtistsPublisher = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val siblingsPublisher = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val recentlyAddedPublisher = MutableStateFlow<List<DisplayableItem>>(emptyList())
    private val songPublisher = MutableStateFlow<List<DisplayableItem>?>(null)

    private val biographyPublisher = MutableStateFlow<String?>(null)

    init {
        // header
        dataProvider.observeHeader(parentMediaId)
            .flowOn(Dispatchers.Default)
            .onEach { itemPublisher.value = it.first() }
            .launchIn(viewModelScope)

        // most played
        dataProvider.observeMostPlayed(parentMediaId)
            .mapListItem { it as DisplayableTrack }
            .flowOn(Dispatchers.Default)
            .onEach { mostPlayedPublisher.value = it }
            .launchIn(viewModelScope)

        // related artists
        dataProvider.observeRelatedArtists(parentMediaId)
            .map { it.take(RELATED_ARTISTS_TO_SEE) }
            .flowOn(Dispatchers.Default)
            .onEach { relatedArtistsPublisher.value = it }
            .launchIn(viewModelScope)

        // siblings
        dataProvider.observeSiblings(parentMediaId)
            .flowOn(Dispatchers.Default)
            .onEach { siblingsPublisher.value = it }
            .launchIn(viewModelScope)

        // recent
        dataProvider.observeRecentlyAdded(parentMediaId)
            .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
            .flowOn(Dispatchers.Default)
            .onEach { recentlyAddedPublisher.value = it }
            .launchIn(viewModelScope)

        // songs
        dataProvider.observe(parentMediaId, filterPublisher)
            .flowOn(Dispatchers.Default)
            .onEach { songPublisher.value = it }
            .launchIn(viewModelScope)

        // biography
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val biography = when {
                    parentMediaId.isArtist -> imageRetrieverGateway.getArtist(parentMediaId.categoryId)?.wiki
                    parentMediaId.isAlbum -> imageRetrieverGateway.getAlbum(parentMediaId.categoryId)?.wiki
                    else -> null
                }
                withContext(Dispatchers.Main) {
                    biographyPublisher.value = biography
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
    }

    fun observeItem(): Flow<DisplayableItem> = itemPublisher.filterNotNull()
    fun observeMostPlayed(): Flow<List<DisplayableTrack>> = mostPlayedPublisher
    fun observeRecentlyAdded(): Flow<List<DisplayableItem>> = recentlyAddedPublisher
    fun observeRelatedArtists(): Flow<List<DisplayableItem>> = relatedArtistsPublisher
    fun observeSiblings(): Flow<List<DisplayableItem>> = siblingsPublisher
    fun observeSongs(): Flow<List<DisplayableItem>> = songPublisher.filterNotNull()
    fun observeBiography(): Flow<String> = biographyPublisher.filterNotNull()

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
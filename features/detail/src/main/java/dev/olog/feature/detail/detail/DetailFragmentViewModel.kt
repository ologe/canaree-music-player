package dev.olog.feature.detail.detail

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.interactor.sort.GetDetailSortUseCase
import dev.olog.domain.interactor.sort.ObserveDetailSortUseCase
import dev.olog.domain.interactor.sort.SetSortOrderUseCase
import dev.olog.domain.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.feature.detail.detail.model.*
import dev.olog.feature.detail.detail.model.DetailDataProvider
import dev.olog.feature.detail.detail.model.DetailFragmentAlbumModel
import dev.olog.feature.detail.detail.model.DetailFragmentMostPlayedModel
import dev.olog.feature.detail.detail.model.DetailFragmentRecentlyAddedModel
import dev.olog.feature.detail.detail.model.DetailFragmentRelatedArtistModel
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument
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

    val parentMediaId = state.argument(Params.MEDIA_ID, MediaId::fromString)

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterPublisher = MutableStateFlow("")

    fun updateFilter(filter: String) {
        filterPublisher.value = filter
    }

    fun getFilter(): String = filterPublisher.value

    private val itemPublisher = MutableStateFlow<DetailFragmentModel.MainHeader?>(null)
    private val mostPlayedPublisher = MutableStateFlow<List<DetailFragmentMostPlayedModel>>(emptyList())
    private val relatedArtistsPublisher = MutableStateFlow<List<DetailFragmentRelatedArtistModel>>(emptyList())
    private val siblingsPublisher = MutableStateFlow<List<DetailFragmentAlbumModel>>(emptyList())
    private val recentlyAddedPublisher = MutableStateFlow<List<DetailFragmentRecentlyAddedModel>>(emptyList())
    private val songPublisher = MutableStateFlow<List<DetailFragmentModel>?>(null)

    private val biographyPublisher = MutableStateFlow<String?>(null)

    init {
        // header
        dataProvider.observeHeader(parentMediaId)
            .flowOn(Dispatchers.Default)
            .onEach { itemPublisher.value = it.first() as DetailFragmentModel.MainHeader }
            .launchIn(viewModelScope)

        // most played
        dataProvider.observeMostPlayed(parentMediaId)
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

    fun observeItem(): Flow<DetailFragmentModel.MainHeader> = itemPublisher.filterNotNull()
    fun observeMostPlayed(): Flow<List<DetailFragmentMostPlayedModel>> = mostPlayedPublisher
    fun observeRecentlyAdded(): Flow<List<DetailFragmentRecentlyAddedModel>> = recentlyAddedPublisher
    fun observeRelatedArtists(): Flow<List<DetailFragmentRelatedArtistModel>> = relatedArtistsPublisher
    fun observeSiblings(): Flow<List<DetailFragmentAlbumModel>> = siblingsPublisher
    fun observeSongs(): Flow<List<DetailFragmentModel>> = songPublisher.filterNotNull()
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

    fun removeFromPlaylist(item: DetailFragmentModel.PlaylistTrack) = viewModelScope.launch(Dispatchers.Default) {
        presenter.removeFromPlaylist(item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(parentMediaId)
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
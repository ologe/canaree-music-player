package dev.olog.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.interactor.sort.GetDetailSortUseCase
import dev.olog.core.interactor.sort.ObserveDetailSortUseCase
import dev.olog.core.interactor.sort.SetSortOrderUseCase
import dev.olog.core.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.mapListItem
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val getSortOrderUseCase: GetDetailSortUseCase,
    private val observeSortOrderUseCase: ObserveDetailSortUseCase,
    private val toggleSortArrangingUseCase: ToggleDetailSortArrangingUseCase,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    private val schedulers: Schedulers,
    private val podcastGateway: PodcastGateway

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val VISIBLE_RECENTLY_ADDED_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    private var moveList = mutableListOf<Pair<Int, Int>>()

    private val filterChannel = ConflatedBroadcastChannel("")

    fun updateFilter(filter: String) {
        filterChannel.offer(filter)
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
        dataProvider.observeHeader(mediaId)
            .flowOn(schedulers.cpu)
            .onEach { itemLiveData.value = it[0] }
            .launchIn(viewModelScope)

        // most played
        dataProvider.observeMostPlayed(mediaId)
            .mapListItem { it as DisplayableTrack }
            .flowOn(schedulers.cpu)
            .onEach { mostPlayedLiveData.value = it }
            .launchIn(viewModelScope)

        // related artists
        dataProvider.observeRelatedArtists(mediaId)
            .map { it.take(RELATED_ARTISTS_TO_SEE) }
            .flowOn(schedulers.cpu)
            .onEach { relatedArtistsLiveData.value = it }
            .launchIn(viewModelScope)

        // siblings
        dataProvider.observeSiblings(mediaId)
            .flowOn(schedulers.cpu)
            .onEach { siblingsLiveData.value = it }
            .launchIn(viewModelScope)

        // recent
        dataProvider.observeRecentlyAdded(mediaId)
            .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
            .flowOn(schedulers.cpu)
            .onEach { recentlyAddedLiveData.value = it }
            .launchIn(viewModelScope)

        // songs
        dataProvider.observe(mediaId, filterChannel.asFlow())
            .flowOn(schedulers.cpu)
            .onEach { songLiveData.value = it }
            .launchIn(viewModelScope)

        // biography
        viewModelScope.launch(schedulers.io) {
            try {
                val biography = when {
                    mediaId.isArtist -> imageRetrieverGateway.getArtist(mediaId.categoryId)?.wiki
                    mediaId.isAlbum -> imageRetrieverGateway.getAlbum(mediaId.categoryId)?.wiki
                    else -> null
                }
                withContext(schedulers.main) {
                    biographyLiveData.value = biography
                }
            } catch (ex: NullPointerException) {
                Timber.e(ex)
                ex.printStackTrace()
            } catch (ex: IndexOutOfBoundsException) {
                Timber.e(ex)
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

    fun observeAllCurrentPositions() = podcastGateway.observeAllCurrentPositions()
        .map {
            it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
        }.flowOn(schedulers.cpu)

    fun detailSortDataUseCase(mediaId: MediaId, action: (SortEntity) -> Unit) {
        val sortOrder = getSortOrderUseCase(mediaId)
        action(sortOrder)
    }

    fun observeSortOrder(action: (SortType) -> Unit) {
        val sortEntity = getSortOrderUseCase(mediaId)
        action(sortEntity.type)
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(schedulers.io) {
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

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(schedulers.cpu) {
        require(item is DisplayableTrack)
        presenter.removeFromPlaylist(item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(mediaId)
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
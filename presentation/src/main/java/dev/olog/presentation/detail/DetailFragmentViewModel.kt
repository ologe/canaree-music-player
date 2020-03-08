package dev.olog.presentation.detail

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

    private val filterPublisher = ConflatedBroadcastChannel("")

    fun updateFilter(filter: String) {
        filterPublisher.offer(filter)
    }

    fun getFilter(): String = filterPublisher.value

    private val biographyPublisher = ConflatedBroadcastChannel<String?>()

    init {
        // biography
        viewModelScope.launch(schedulers.io) {
            try {
                val biography = when {
                    mediaId.isArtist -> imageRetrieverGateway.getArtist(mediaId.categoryId)?.wiki
                    mediaId.isAlbum -> imageRetrieverGateway.getAlbum(mediaId.categoryId)?.wiki
                    else -> null
                }
                biographyPublisher.offer(biography)
            } catch (ex: NullPointerException) {
                Timber.e(ex)
                ex.printStackTrace()
            } catch (ex: IndexOutOfBoundsException) {
                Timber.e(ex)
                ex.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        filterPublisher.close()
        biographyPublisher.close()
    }

    val item: Flow<DisplayableItem> = dataProvider.observeHeader(mediaId)
        .map { it[0] }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val mostPlayed: Flow<List<DisplayableTrack>> = dataProvider.observeMostPlayed(mediaId)
        .mapListItem { it as DisplayableTrack }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val recentlyAdded: Flow<List<DisplayableItem>> = dataProvider.observeRecentlyAdded(mediaId)
        .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val relatedArtists: Flow<List<DisplayableItem>> = dataProvider.observeRelatedArtists(mediaId)
        .map { it.take(RELATED_ARTISTS_TO_SEE) }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val siblings: Flow<List<DisplayableItem>> = dataProvider.observeSiblings(mediaId)
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val songs: Flow<List<DisplayableItem>> = dataProvider.observe(mediaId, filterPublisher.asFlow())
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val biography: Flow<String?> = biographyPublisher.asFlow()

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
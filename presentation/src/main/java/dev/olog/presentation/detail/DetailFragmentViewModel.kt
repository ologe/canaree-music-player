package dev.olog.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.domain.entity.AutoPlaylist
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.interactor.sort.GetDetailSortUseCase
import dev.olog.domain.interactor.sort.ObserveDetailSortUseCase
import dev.olog.domain.interactor.sort.SetSortOrderUseCase
import dev.olog.domain.interactor.sort.ToggleDetailSortArrangingUseCase
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.presentation.base.model.PresentationId
import dev.olog.feature.presentation.base.model.PresentationIdCategory.*
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.feature.presentation.base.model.toDomain
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: PresentationId.Category,
    dataProvider: DetailDataProvider,
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
                val biography = when (mediaId.category) {
                    ARTISTS -> imageRetrieverGateway.getArtist(mediaId.categoryId.toLong())?.wiki
                    ALBUMS -> imageRetrieverGateway.getAlbum(mediaId.categoryId.toLong())?.wiki
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
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val recentlyAdded: Flow<List<DisplayableTrack>> = dataProvider.observeRecentlyAdded(mediaId)
        .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val relatedArtists: Flow<List<DisplayableAlbum>> = dataProvider.observeRelatedArtists(mediaId)
        .map { it.take(RELATED_ARTISTS_TO_SEE) }
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val siblings: Flow<List<DisplayableAlbum>> = dataProvider.observeSiblings(mediaId)
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val songs: Flow<List<DisplayableItem>> = dataProvider.observe(mediaId, filterPublisher.asFlow())
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val spotifySingle: Flow<List<DisplayableItem>> = dataProvider.observeSpotifyArtistSingles(mediaId)
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val spotifyAlbums: Flow<List<DisplayableItem>> = dataProvider.observeSpotifyArtistAlbums(mediaId)
        .distinctUntilChanged()
        .flowOn(schedulers.cpu)

    val biography: Flow<String?> = biographyPublisher.asFlow()

    fun observeAllCurrentPositions() = podcastGateway.observeAllCurrentPositions()
        .map {
            it.groupBy { it.id }.mapValues { it.value[0].position.toInt() }
        }.flowOn(schedulers.cpu)

    fun detailSortDataUseCase(mediaId: PresentationId, action: (SortEntity) -> Unit) {
        val sortOrder = getSortOrderUseCase(mediaId.toDomain())
        action(sortOrder)
    }

    fun observeSortOrder(action: (SortType) -> Unit) {
        val sortEntity = getSortOrderUseCase(mediaId.toDomain())
        action(sortEntity.type)
    }

    fun updateSortOrder(sortType: SortType) = viewModelScope.launch(schedulers.io) {
        setSortOrderUseCase(SetSortOrderUseCase.Request(mediaId.toDomain(), sortType))
    }

    fun toggleSortArranging() {
        if (mediaId.category == PLAYLISTS &&
            getSortOrderUseCase(mediaId.toDomain()).type == SortType.CUSTOM){
            return
        }
        toggleSortArrangingUseCase(mediaId.category.toDomain())
    }

    fun addMove(from: Int, to: Int){
        moveList.add(from to to)
    }

    fun processMove() = viewModelScope.launch {
        if (mediaId.category == PLAYLISTS || mediaId.category == PODCASTS_PLAYLIST){
            presenter.moveInPlaylist(moveList)
        }
        moveList.clear()
    }

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(schedulers.cpu) {
        require(item is DisplayableTrack)
        presenter.removeFromPlaylist(item)
    }

    fun observeSorting(): Flow<SortEntity> {
        return observeSortOrderUseCase(mediaId.toDomain())
    }

    fun showSortByTutorialIfNeverShown(): Boolean {
        if (mediaId.isAnyPodcast || AutoPlaylist.isAutoPlaylist(mediaId.categoryId)) {
            return false
        }
        return presenter.showSortByTutorialIfNeverShown()
    }

}
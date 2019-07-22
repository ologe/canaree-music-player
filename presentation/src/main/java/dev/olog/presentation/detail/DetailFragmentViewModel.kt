package dev.olog.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.core.gateway.LastFmGateway
import dev.olog.core.interactor.sort.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.extensions.mapListItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlowable
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DetailFragmentViewModel @Inject constructor(
    val mediaId: MediaId,
    private val dataProvider: DetailDataProvider,
    private val presenter: DetailFragmentPresenter,
    private val setSortOrderUseCase: SetSortOrderUseCase,
    private val observeSortOrderUseCase: ObserveDetailSortOrderUseCase,
    private val setSortArrangingUseCase: SetSortArrangingUseCase,
    private val getSortArrangingUseCase: GetSortArrangingUseCase,
    private val getDetailSortDataUseCase: GetDetailSortDataUseCase,
    private val lastFmGateway: LastFmGateway

) : ViewModel() {

    companion object {
        const val NESTED_SPAN_COUNT = 4
        const val VISIBLE_RECENTLY_ADDED_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }


    private val subscriptions = CompositeDisposable()

    private val filterChannel = ConflatedBroadcastChannel("")

    fun updateFilter(filter: String) {
        filterChannel.offer(filter)
    }

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
                    mediaId.isArtist -> lastFmGateway.getArtist(mediaId.categoryId)?.wiki
                    mediaId.isAlbum -> lastFmGateway.getAlbum(mediaId.categoryId)?.wiki
                    else -> null
                }
                withContext(Dispatchers.Main){
                    biographyLiveData.value = biography
                }
            } catch (ex: NullPointerException){
                ex.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        subscriptions.clear()
        viewModelScope.cancel()
    }

    fun observeItem(): LiveData<DisplayableItem> = itemLiveData
    fun observeMostPlayed(): LiveData<List<DisplayableTrack>> = mostPlayedLiveData
    fun observeRecentlyAdded(): LiveData<List<DisplayableItem>> = recentlyAddedLiveData
    fun observeRelatedArtists(): LiveData<List<DisplayableItem>> = relatedArtistsLiveData
    fun observeSiblings(): LiveData<List<DisplayableItem>> = siblingsLiveData
    fun observeSongs(): LiveData<List<DisplayableItem>> = songLiveData
    fun observeBiography(): LiveData<String?> = biographyLiveData

    fun detailSortDataUseCase(mediaId: MediaId, action: (SortEntity) -> Unit) {
        getDetailSortDataUseCase.execute(mediaId)
            .subscribe(action, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    fun observeSortOrder(action: (SortType) -> Unit) {
        observeSortOrderUseCase(mediaId)
            .asFlowable().toObservable()
            .firstOrError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ action(it) }, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    fun updateSortOrder(sortType: SortType) {
        setSortOrderUseCase.execute(SetSortOrderRequestModel(mediaId, sortType))
            .subscribe({ }, Throwable::printStackTrace)
            .addTo(subscriptions)
    }

    fun toggleSortArranging() {
        observeSortOrderUseCase(mediaId)
            .asFlowable().toObservable()
            .firstOrError()
            .filter { it != SortType.CUSTOM }
            .flatMapCompletable { setSortArrangingUseCase.execute() }
            .subscribe({ }, Throwable::printStackTrace)
            .addTo(subscriptions)

    }

    fun moveItemInPlaylist(from: Int, to: Int) {
        presenter.moveInPlaylist(from, to)
    }

    fun removeFromPlaylist(item: DisplayableItem) = viewModelScope.launch(Dispatchers.Default) {
        require(item is DisplayableTrack)
        presenter.removeFromPlaylist(item)
    }

    fun observeSorting(): Observable<SortEntity> {
        return Observables.combineLatest(
            observeSortOrderUseCase(mediaId).asFlowable().toObservable(),
            getSortArrangingUseCase.execute()
        ) { sort, arranging -> SortEntity(sort, arranging) }
    }

    fun showSortByTutorialIfNeverShown(): Completable {
        return presenter.showSortByTutorialIfNeverShown()
    }

}
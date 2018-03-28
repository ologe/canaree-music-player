package dev.olog.msc.presentation.detail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.interactor.detail.GetDetailTabsVisibilityUseCase
import dev.olog.msc.domain.interactor.detail.sorting.*
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.k.extension.asLiveData
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables

class DetailFragmentViewModel(
        val mediaId: MediaId,
        item: Map<MediaIdCategory, @JvmSuppressWildcards Flowable<List<DisplayableItem>>>,
        albums: Map<MediaIdCategory, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        data: Map<String, @JvmSuppressWildcards Observable<List<DisplayableItem>>>,
        private val presenter: DetailFragmentPresenter,
        private val setSortOrderUseCase: SetSortOrderUseCase,
        private val observeSortOrderUseCase: GetSortOrderUseCase,
        private val setSortArrangingUseCase: SetSortArrangingUseCase,
        private val getSortArrangingUseCase: GetSortArrangingUseCase,
        getVisibleTabsUseCase : GetDetailTabsVisibilityUseCase,
        val getDetailSortDataUseCase: GetDetailSortDataUseCase

) : ViewModel() {

    companion object {
        const val RECENTLY_ADDED = "RECENTLY_ADDED"
        const val MOST_PLAYED = "MOST_PLAYED"
        const val RELATED_ARTISTS = "RELATED_ARTISTS"
        const val SONGS = "SONGS"

        const val NESTED_SPAN_COUNT = 4
        const val VISIBLE_RECENTLY_ADDED_PAGES = NESTED_SPAN_COUNT * 4
        const val RELATED_ARTISTS_TO_SEE = 10
    }

    private val currentCategory = mediaId.category

    val itemLiveData: LiveData<List<DisplayableItem>> = item[currentCategory]!!.asLiveData()
    private val dataMapLiveData : MutableLiveData<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>> = DetailLiveData()

    private val dataMap : Observable<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>> = Observables.combineLatest(
            Observables.combineLatest(
                    item[currentCategory]!!.toObservable(),
                    data[MOST_PLAYED]!!,
                    data[RECENTLY_ADDED]!!,
                    albums[currentCategory]!!,
                    data[RELATED_ARTISTS]!!,
                    data[SONGS]!!,
                    getVisibleTabsUseCase.execute(),
                    { item, mostPlayed, recent, albums, artists, songs, visibility ->
                        presenter.createDataMap(item, mostPlayed, recent, albums, artists, songs, visibility)
                    }
            ).startWithArray(mutableMapOf()).distinctUntilChanged(),
            item[currentCategory]!!.toObservable(),
            { map, item ->
                mutableMapOf(DetailFragmentDataType.HEADER to item.toMutableList()).apply { putAll(map) }
            }
    ).onErrorReturnItem(mutableMapOf())

    private val dataDisposable = dataMap.subscribe(dataMapLiveData::postValue, Throwable::printStackTrace)

    override fun onCleared() {
        dataDisposable.unsubscribe()
    }

    fun observeData(): LiveData<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>> = dataMapLiveData

    fun artistMediaId() : Maybe<MediaId> {
        return presenter.artistMediaId()
    }

    val mostPlayedLiveData: LiveData<List<DisplayableItem>> = data[MOST_PLAYED]!!
            .asLiveData()

    val relatedArtistsLiveData : LiveData<List<DisplayableItem>> = data[RELATED_ARTISTS]!!
            .map { it.take(RELATED_ARTISTS_TO_SEE) }
            .asLiveData()

    val recentlyAddedLiveData: LiveData<List<DisplayableItem>> = data[RECENTLY_ADDED]!!
            .map { it.take(VISIBLE_RECENTLY_ADDED_PAGES) }
            .asLiveData()

    fun updateSortType(sortType: SortType): Completable {
        return setSortOrderUseCase.execute(SetSortOrderRequestModel(
                mediaId, sortType))
    }

    fun toggleSortArranging(): Completable {
        return setSortArrangingUseCase.execute()
    }

    fun observeSortOrder(): Observable<SortType> {
        return observeSortOrderUseCase.execute(mediaId)
    }

    fun getSortArranging(): Observable<SortArranging> {
        return getSortArrangingUseCase.execute()
    }

    fun moveItemInPlaylist(from: Int, to: Int){
        presenter.moveInPlaylist(from, to)
    }

    fun removeFromPlaylist(item: DisplayableItem): Completable {
        return presenter.removeFromPlaylist(item)
    }

}

/**
 * Because after rotation is emitted an event with only item data,
 * this LiveData handle that case not emitting that event
 */
private class DetailLiveData : MutableLiveData<MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>>() {

    override fun setValue(value: MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>) {
        if (canUpdate(value)){
            super.setValue(value)
        }
    }

    override fun postValue(value: MutableMap<DetailFragmentDataType, MutableList<DisplayableItem>>) {
        if (canUpdate(value)){
            super.postValue(value)
        }
    }

    private fun canUpdate(value: MutableMap<*, MutableList<DisplayableItem>>): Boolean {
        if (this.value == null) {
            return true
        }
        return value.size > 1
    }

}
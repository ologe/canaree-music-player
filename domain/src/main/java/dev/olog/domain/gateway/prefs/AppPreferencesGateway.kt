package dev.olog.domain.gateway.prefs

import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.domain.entity.SortArranging
import dev.olog.domain.entity.SortType
import io.reactivex.Completable
import io.reactivex.Flowable

interface AppPreferencesGateway : Sorting {

    fun isFirstAccess(): Boolean

    fun getVisibleTabs(): Flowable<BooleanArray>

    fun getViewPagerLastVisitedPage(): Int
    fun setViewPagerLastVisitedPage(lastPage: Int)

    fun isIconsDark(): Flowable<Boolean>

    fun getLowerVolumeOnNight(): Boolean
    fun observeLowerVolumeOnNight(): Flowable<Boolean>

    fun getLibraryCategoriesBehavior() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategoriesBehavior() : List<LibraryCategoryBehavior>
    fun setLibraryCategoriesBehavior(behavior: List<LibraryCategoryBehavior>)

    fun getBlackList(): Set<String>
    fun setBlackList(set: Set<String>)

    fun resetSleepTimer()
    fun setSleepTimer(millis: Long)
    fun getSleepTimer() : Long

}

interface Sorting {

    fun getFolderSortOrder() : Flowable<SortType>
    fun getPlaylistSortOrder() : Flowable<SortType>
    fun getAlbumSortOrder() : Flowable<SortType>
    fun getArtistSortOrder() : Flowable<SortType>
    fun getGenreSortOrder() : Flowable<SortType>

    fun setFolderSortOrder(sortType: SortType) : Completable
    fun setPlaylistSortOrder(sortType: SortType) : Completable
    fun setAlbumSortOrder(sortType: SortType) : Completable
    fun setArtistSortOrder(sortType: SortType) : Completable
    fun setGenreSortOrder(sortType: SortType) : Completable

    fun getSortArranging(): Flowable<SortArranging>
    fun toggleSortArranging(): Completable

}
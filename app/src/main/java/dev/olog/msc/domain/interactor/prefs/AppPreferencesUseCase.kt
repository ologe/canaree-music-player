package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.SortArranging
import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferencesUseCase @Inject constructor(
        private val gateway: AppPreferencesGateway
) {

    fun isFirstAccess(): Boolean = gateway.isFirstAccess()

    fun getLibraryCategories() : List<LibraryCategoryBehavior> {
        return gateway.getLibraryCategories()
    }
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior> {
        return gateway.getDefaultLibraryCategories()
    }
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>) {
        gateway.setLibraryCategories(behavior)
    }

    fun getViewPagerLastVisitedPage(): Int = gateway.getViewPagerLastVisitedPage()
    fun setViewPagerLastVisitedPage(lastPage: Int) {
        gateway.setViewPagerLastVisitedPage(lastPage)
    }

    fun getBlackList(): Set<String> {
        return gateway.getBlackList()
    }
    fun setBlackList(set: Set<String>) {
        gateway.setBlackList(set)
    }

    fun getFolderSortOrder() : Flowable<SortType> = gateway.getFolderSortOrder()
    fun getPlaylistSortOrder() : Flowable<SortType> = gateway.getPlaylistSortOrder()
    fun getAlbumSortOrder() : Flowable<SortType> = gateway.getAlbumSortOrder()
    fun getArtistSortOrder() : Flowable<SortType> = gateway.getArtistSortOrder()
    fun getGenreSortOrder() : Flowable<SortType> = gateway.getGenreSortOrder()

    fun setFolderSortOrder(sortType: SortType) : Completable = gateway.setFolderSortOrder(sortType)
    fun setPlaylistSortOrder(sortType: SortType) : Completable = gateway.setPlaylistSortOrder(sortType)
    fun setAlbumSortOrder(sortType: SortType) : Completable = gateway.setAlbumSortOrder(sortType)
    fun setArtistSortOrder(sortType: SortType) : Completable = gateway.setArtistSortOrder(sortType)
    fun setGenreSortOrder(sortType: SortType) : Completable = gateway.setGenreSortOrder(sortType)

    fun getSortArranging(): Flowable<SortArranging> = gateway.getSortArranging()
    fun toggleSortArranging(): Completable = gateway.toggleSortArranging()

}
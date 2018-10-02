package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.entity.LibrarySortType
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
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

    fun getPodcastLibraryCategories() : List<LibraryCategoryBehavior> {
        return gateway.getPodcastLibraryCategories()
    }

    fun getLastBottomViewPage(): Int {
        return gateway.getLastBottomViewPage()
    }

    fun setLastBottomViewPage(page: Int) {
        gateway.setLastBottomViewPage(page)
    }

    fun getDefaultPodcastLibraryCategories() : List<LibraryCategoryBehavior>{
        return gateway.getDefaultPodcastLibraryCategories()
    }
    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>){
        gateway.setPodcastLibraryCategories(behavior)
    }

    fun getViewPagerLibraryLastPage(): Int = gateway.getViewPagerLibraryLastPage()
    fun setViewPagerLastVisitedPage(lastPage: Int) {
        gateway.setViewPagerLibraryLastPage(lastPage)
    }

    fun getViewPagerPodcastLastPage(): Int = gateway.getViewPagerPodcastLastPage()
    fun setViewPagerPodcastLastPage(lastPage: Int){
        gateway.setViewPagerPodcastLastPage(lastPage)
    }

    fun getBlackList(): Set<String> {
        return gateway.getBlackList()
    }
    fun setBlackList(set: Set<String>) {
        gateway.setBlackList(set)
    }

    fun observePlayerControlsVisibility(): Observable<Boolean> {
        return gateway.observePlayerControlsVisibility()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setDefault(): Completable {
        return gateway.setDefault()
    }

    fun observeAutoCreateImages(): Observable<Boolean> {
        return gateway.observeAutoCreateImages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getSyncAdjustment(): Long {
        return gateway.getSyncAdjustment()
    }
    fun setSyncAdjustment(value: Long) {
        gateway.setSyncAdjustment(value)
    }

    fun getAllTracksSortOrder(): LibrarySortType {
        return gateway.getAllTracksSortOrder()
    }

    fun getAllAlbumsSortOrder(): LibrarySortType {
        return gateway.getAllAlbumsSortOrder()
    }

    fun getAllArtistsSortOrder(): LibrarySortType {
        return gateway.getAllArtistsSortOrder()
    }

    fun getDefaultMusicFolder(): File = gateway.getDefaultMusicFolder()
    fun setDefaultMusicFolder(file: File) {
        gateway.setDefaultMusicFolder(file)
    }

}
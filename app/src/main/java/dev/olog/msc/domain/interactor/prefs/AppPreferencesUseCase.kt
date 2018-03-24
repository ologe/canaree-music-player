package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
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

    fun observePlayerControlsVisibility(): Observable<Boolean> {
        return gateway.observePlayerControlsVisibility()
                .subscribeOn(Schedulers.io())
    }

    fun setDefault(): Completable {
        return gateway.setDefault()
    }

}
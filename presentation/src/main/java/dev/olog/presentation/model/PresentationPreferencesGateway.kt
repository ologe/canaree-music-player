package dev.olog.presentation.model

import dev.olog.presentation.main.BottomNavigationPage
import dev.olog.presentation.main.LibraryPage
import io.reactivex.Completable
import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow

interface PresentationPreferencesGateway {
    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun getLastLibraryPage(): LibraryPage
    fun setLibraryPage(page: LibraryPage)

    fun isFirstAccess(): Boolean
    fun observeVisibleTabs(): Flow<BooleanArray>
    fun getViewPagerLibraryLastPage(): Int
    fun setViewPagerLibraryLastPage(lastPage: Int)
    fun getViewPagerPodcastLastPage(): Int
    fun setViewPagerPodcastLastPage(lastPage: Int)
    fun getLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior>
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>)
    fun getPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>)
    fun observeLibraryNewVisibility(): Observable<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Observable<Boolean>
    fun canShowPodcastCategory(): Boolean

    fun setDefault(): Completable
}
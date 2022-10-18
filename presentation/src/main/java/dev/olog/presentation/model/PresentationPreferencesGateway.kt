package dev.olog.presentation.model

import dev.olog.presentation.tab.TabCategory
import kotlinx.coroutines.flow.Flow

interface PresentationPreferencesGateway {

    fun getLastBottomViewPage(): BottomNavigationPage
    fun setLastBottomViewPage(page: BottomNavigationPage)

    fun getLastLibraryPage(): LibraryPage
    fun setLibraryPage(page: LibraryPage)

    fun isFirstAccess(): Boolean

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

    fun observeLibraryNewVisibility(): Flow<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Flow<Boolean>

    fun isAdaptiveColorEnabled(): Boolean

    fun observePlayerControlsVisibility(): Flow<Boolean>

    fun getSpanCount(category: TabCategory): Int
    fun observeSpanCount(category: TabCategory): Flow<Int>
    fun setSpanCount(category: TabCategory, spanCount: Int)

    fun canShowPodcasts(): Boolean

    fun setDefault()
}
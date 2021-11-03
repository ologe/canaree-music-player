package dev.olog.presentation.model

import dev.olog.core.Prefs
import kotlinx.coroutines.flow.Flow

internal interface PresentationPreferencesGateway : Prefs {

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

    fun isAdaptiveColorEnabled(): Boolean

    fun observePlayerControlsVisibility(): Flow<Boolean>

    fun canShowPodcasts(): Boolean

    fun setDefault()
}
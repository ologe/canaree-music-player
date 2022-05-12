package dev.olog.feature.library

import dev.olog.core.Resettable
import kotlinx.coroutines.flow.Flow

interface LibraryPreferences : Resettable {

    fun getSpanCount(category: TabCategory): Int
    fun observeSpanCount(category: TabCategory): Flow<Int>
    fun setSpanCount(category: TabCategory, spanCount: Int)

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

    fun getLastLibraryPage(): LibraryPage
    fun setLibraryPage(page: LibraryPage)

    fun canShowPodcasts(): Boolean

    fun observeLibraryNewVisibility(): Flow<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Flow<Boolean>

}
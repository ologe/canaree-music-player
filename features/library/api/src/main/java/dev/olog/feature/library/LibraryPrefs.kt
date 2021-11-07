package dev.olog.feature.library

import dev.olog.core.Preference
import dev.olog.core.Prefs

interface LibraryPrefs : Prefs {

    fun spanCount(category: TabCategory): Preference<Int>

    val newItemsVisibility: Preference<Boolean>
    val recentPlayedVisibility: Preference<Boolean>

    fun getViewPagerLibraryLastPage(): Int
    fun setViewPagerLibraryLastPage(lastPage: Int)
    fun getViewPagerPodcastLastPage(): Int
    fun setViewPagerPodcastLastPage(lastPage: Int)

    fun getLastLibraryPage(): LibraryPage
    fun setLibraryPage(page: LibraryPage)

    fun getLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultLibraryCategories() : List<LibraryCategoryBehavior>
    fun setLibraryCategories(behavior: List<LibraryCategoryBehavior>)

    fun getPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun getDefaultPodcastLibraryCategories() : List<LibraryCategoryBehavior>
    fun setPodcastLibraryCategories(behavior: List<LibraryCategoryBehavior>)

    fun canShowPodcasts(): Boolean

    val useFolderTree: Preference<Boolean>

    val blacklist: Preference<Set<String>>

}
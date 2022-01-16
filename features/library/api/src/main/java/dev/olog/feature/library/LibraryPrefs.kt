package dev.olog.feature.library

import dev.olog.core.MediaStoreType
import dev.olog.core.MediaUri
import dev.olog.core.preference.Preference
import dev.olog.core.preference.Prefs

interface LibraryPrefs : Prefs {

    fun spanCount(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): Preference<Int>

    val recentlyAddedVisibility: Preference<Boolean>
    val recentlyPlayedVisibility: Preference<Boolean>

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

}
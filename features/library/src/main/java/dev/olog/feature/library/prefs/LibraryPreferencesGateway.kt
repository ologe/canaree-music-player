package dev.olog.feature.library.prefs

import dev.olog.domain.ResettablePreference
import dev.olog.feature.library.library.LibraryFragmentCategoryState
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.navigation.BottomNavigationPage
import kotlinx.coroutines.flow.Flow

internal interface LibraryPreferencesGateway : ResettablePreference {

    var bottomNavigationPage: BottomNavigationPage

    var libraryTracksLastPage: Int
    var libraryPodcastsLastPage: Int

    fun getLibraryCategories() : List<LibraryFragmentCategoryState>
    fun getDefaultLibraryCategories() : List<LibraryFragmentCategoryState>
    fun setLibraryCategories(behavior: List<LibraryFragmentCategoryState>)

    fun getPodcastLibraryCategories() : List<LibraryFragmentCategoryState>
    fun getDefaultPodcastLibraryCategories() : List<LibraryFragmentCategoryState>
    fun setPodcastLibraryCategories(behavior: List<LibraryFragmentCategoryState>)

    fun observeLibraryNewVisibility(): Flow<Boolean>
    fun observeLibraryRecentPlayedVisibility(): Flow<Boolean>

    fun getSpanCount(category: TabFragmentCategory): Int
    fun observeSpanCount(category: TabFragmentCategory): Flow<Int>
    fun setSpanCount(category: TabFragmentCategory, spanCount: Int)

}
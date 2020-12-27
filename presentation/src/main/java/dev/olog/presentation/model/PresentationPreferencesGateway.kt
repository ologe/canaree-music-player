package dev.olog.presentation.model

import dev.olog.navigation.BottomNavigationPage
import dev.olog.presentation.tab.TabCategory
import kotlinx.coroutines.flow.Flow

internal interface PresentationPreferencesGateway {

    var bottomNavigationPage: BottomNavigationPage

    fun isFirstAccess(): Boolean

    var libraryTracksLastPage: Int
    var libraryPodcastsLastPage: Int

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
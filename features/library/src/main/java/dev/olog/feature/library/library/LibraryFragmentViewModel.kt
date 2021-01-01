package dev.olog.feature.library.library

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.navigation.BottomNavigationPage
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument

internal class LibraryFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val appPrefs: AppPreferencesGateway,
    private val libraryPrefs: LibraryPreferencesGateway,
    private val tutorialPrefs: TutorialPreferenceGateway
): ViewModel() {

    private val isPodcast = state.argument<Boolean>(Params.IS_PODCAST)

    fun getViewPagerLastPage(totalPages: Int): Int {
        val lastPage = if (isPodcast) {
            libraryPrefs.libraryPodcastsLastPage
        } else {
            libraryPrefs.libraryTracksLastPage
        }
        return lastPage.coerceIn(0, totalPages)
    }

    fun setViewPagerLastPage(page: Int) {
        if (isPodcast) {
            libraryPrefs.libraryPodcastsLastPage = page
        } else {
            libraryPrefs.libraryTracksLastPage = page
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPrefs.floatingWindowTutorial()
    }

    fun getCategories(): List<LibraryFragmentCategoryState> {
        if (isPodcast) {
            return libraryPrefs.getPodcastLibraryCategories()
                .filter { it.visible }
        }
        return libraryPrefs.getLibraryCategories()
            .filter { it.visible }
    }

    fun setLibraryPage(page: BottomNavigationPage) {
        libraryPrefs.bottomNavigationPage = page
    }

    fun canShowPodcasts() = appPrefs.canShowPodcasts

}
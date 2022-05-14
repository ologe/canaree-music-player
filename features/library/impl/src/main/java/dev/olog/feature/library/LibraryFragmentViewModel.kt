package dev.olog.feature.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.api.LibraryCategoryBehavior
import dev.olog.feature.library.api.LibraryPage
import dev.olog.feature.library.api.LibraryPreferences
import javax.inject.Inject

@HiltViewModel
internal class LibraryFragmentViewModel @Inject constructor(
    private val libraryPrefs: LibraryPreferences,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) : ViewModel() {

    fun getViewPagerLastPage(totalPages: Int, isPodcast: Boolean): Int {
        val lastPage = if (isPodcast) {
            libraryPrefs.getViewPagerPodcastLastPage()
        } else {
            libraryPrefs.getViewPagerLibraryLastPage()
        }
        return lastPage.coerceIn(0, totalPages)
    }

    fun setViewPagerLastPage(page: Int, isPodcast: Boolean) {
        if (isPodcast) {
            libraryPrefs.setViewPagerPodcastLastPage(page)
        } else {
            libraryPrefs.setViewPagerLibraryLastPage(page)
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun getCategories(isPodcast: Boolean): List<LibraryCategoryBehavior> {
        if (isPodcast) {
            return libraryPrefs.getPodcastLibraryCategories()
                .filter { it.visible }
        }
        return libraryPrefs.getLibraryCategories()
            .filter { it.visible }
    }

    fun setLibraryPage(page: LibraryPage) {
        libraryPrefs.setLibraryPage(page)
    }

    fun canShowPodcasts() = libraryPrefs.canShowPodcasts()

}
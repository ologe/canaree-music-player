package dev.olog.presentation.library

import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.LibraryPage
import dev.olog.shared.clamp
import javax.inject.Inject

internal class LibraryFragmentPresenter @Inject constructor(
    private val libraryPrefs: LibraryPrefs,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

    fun getViewPagerLastPage(totalPages: Int, isPodcast: Boolean): Int {
        val lastPage = if (isPodcast) {
            libraryPrefs.getViewPagerPodcastLastPage()
        } else {
            libraryPrefs.getViewPagerLibraryLastPage()
        }
        return clamp(lastPage, 0, totalPages)
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
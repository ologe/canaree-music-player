package dev.olog.presentation.library

import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.model.LibraryPage
import javax.inject.Inject

internal class LibraryFragmentPresenter @Inject constructor(
    private val appPrefsUseCase: PresentationPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

    fun getViewPagerLastPage(totalPages: Int, isPodcast: Boolean): Int {
        val lastPage = if (isPodcast) {
            appPrefsUseCase.getViewPagerPodcastLastPage()
        } else {
            appPrefsUseCase.getViewPagerLibraryLastPage()
        }
        return lastPage.coerceIn(0, totalPages)
    }

    fun setViewPagerLastPage(page: Int, isPodcast: Boolean) {
        if (isPodcast) {
            appPrefsUseCase.setViewPagerPodcastLastPage(page)
        } else {
            appPrefsUseCase.setViewPagerLibraryLastPage(page)
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun getCategories(isPodcast: Boolean): List<LibraryCategoryBehavior> {
        if (isPodcast) {
            return appPrefsUseCase.getPodcastLibraryCategories()
                .filter { it.visible }
        }
        return appPrefsUseCase.getLibraryCategories()
            .filter { it.visible }
    }

    fun setLibraryPage(page: LibraryPage) {
        appPrefsUseCase.setLibraryPage(page)
    }

    fun canShowPodcasts() = appPrefsUseCase.canShowPodcasts()

}
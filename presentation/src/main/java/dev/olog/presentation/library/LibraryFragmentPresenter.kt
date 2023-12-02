package dev.olog.presentation.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.model.LibraryPage
import dev.olog.shared.clamp
import javax.inject.Inject

@HiltViewModel
internal class LibraryFragmentPresenter @Inject constructor(
    private val appPrefsUseCase: PresentationPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) : ViewModel() {

    fun getViewPagerLastPage(totalPages: Int, isPodcast: Boolean): Int {
        val lastPage = if (isPodcast) {
            appPrefsUseCase.getViewPagerPodcastLastPage()
        } else {
            appPrefsUseCase.getViewPagerLibraryLastPage()
        }
        return clamp(lastPage, 0, totalPages)
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
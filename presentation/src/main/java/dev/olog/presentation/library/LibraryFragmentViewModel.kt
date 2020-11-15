package dev.olog.presentation.library

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.presentation.model.LibraryPage
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.shared.android.extensions.argument

internal class LibraryFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val appPrefsUseCase: PresentationPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
): ViewModel() {

    private val isPodcast = state.argument<Boolean>(LibraryFragment.IS_PODCAST)

    fun getViewPagerLastPage(totalPages: Int): Int {
        val lastPage = if (isPodcast) {
            appPrefsUseCase.getViewPagerPodcastLastPage()
        } else {
            appPrefsUseCase.getViewPagerLibraryLastPage()
        }
        return lastPage.coerceIn(0, totalPages)
    }

    fun setViewPagerLastPage(page: Int) {
        if (isPodcast) {
            appPrefsUseCase.setViewPagerPodcastLastPage(page)
        } else {
            appPrefsUseCase.setViewPagerLibraryLastPage(page)
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun getCategories(): List<LibraryCategoryBehavior> {
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
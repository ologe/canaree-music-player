package dev.olog.feature.library.library

import dev.olog.domain.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.model.LibraryPage
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.feature.presentation.base.prefs.SharedPreferences
import dev.olog.shared.clamp
import javax.inject.Inject

internal class LibraryFragmentPresenter @Inject constructor(
    private val preferences: LibraryPreferences,
    private val sharedPreferences: SharedPreferences,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

    fun getViewPagerLastPage(totalPages: Int, isPodcast: Boolean): Int {
        val lastPage = if (isPodcast) {
            preferences.getViewPagerPodcastLastPage()
        } else {
            preferences.getViewPagerLibraryLastPage()
        }
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int, isPodcast: Boolean) {
        if (isPodcast) {
            preferences.setViewPagerPodcastLastPage(page)
        } else {
            preferences.setViewPagerLibraryLastPage(page)
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun getCategories(isPodcast: Boolean): List<LibraryCategoryBehavior> {
        if (isPodcast) {
            return sharedPreferences.getPodcastLibraryCategories()
                .filter { it.visible }
        }
        return sharedPreferences.getLibraryCategories()
            .filter { it.visible }
    }

    fun setLibraryPage(page: LibraryPage) {
        preferences.setLibraryPage(page)
    }

    fun canShowPodcasts() = sharedPreferences.canShowPodcasts()

}
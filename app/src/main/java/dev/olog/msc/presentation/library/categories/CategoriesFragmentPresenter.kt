package dev.olog.msc.presentation.library.categories

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.presentation.main.LibraryPage
import dev.olog.shared.utils.clamp
import io.reactivex.Completable
import javax.inject.Inject

class CategoriesFragmentPresenter @Inject constructor(
    private val appPrefsUseCase: PresentationPreferences,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

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

    fun showFloatingWindowTutorialIfNeverShown(): Completable {
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

}
package dev.olog.msc.presentation.library.categories.track

import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.utils.k.extension.clamp
import io.reactivex.Completable
import javax.inject.Inject

class CategoriesFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesGateway,
        private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerLibraryLastPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerLibraryLastPage(page)
    }

    fun showFloatingWindowTutorialIfNeverShown(): Completable{
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun getCategories() = appPrefsUseCase
            .getLibraryCategories()
            .filter { it.visible }

}
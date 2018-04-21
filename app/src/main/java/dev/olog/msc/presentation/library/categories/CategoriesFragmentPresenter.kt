package dev.olog.msc.presentation.library.categories

import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.utils.k.extension.clamp
import javax.inject.Inject

class CategoriesFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesUseCase
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerLastVisitedPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerLastVisitedPage(page)
    }

}
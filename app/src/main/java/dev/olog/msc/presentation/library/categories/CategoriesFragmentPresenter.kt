package dev.olog.msc.presentation.library.categories

import android.support.v4.math.MathUtils
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import javax.inject.Inject

class CategoriesFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesUseCase
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerLastVisitedPage()
        return MathUtils.clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerLastVisitedPage(page)
    }

}
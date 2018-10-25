package dev.olog.msc.presentation.library.categories.podcast

import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import dev.olog.msc.utils.k.extension.clamp
import javax.inject.Inject

class CategoriesPodcastFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: AppPreferencesUseCase
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerPodcastLastPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerPodcastLastPage(page)
    }

}
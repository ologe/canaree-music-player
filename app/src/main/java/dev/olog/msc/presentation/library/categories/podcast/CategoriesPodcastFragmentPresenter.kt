package dev.olog.msc.presentation.library.categories.podcast

import dev.olog.msc.domain.gateway.prefs.PresentationPreferences
import dev.olog.shared.clamp
import javax.inject.Inject

class CategoriesPodcastFragmentPresenter @Inject constructor(
        private val appPrefsUseCase: PresentationPreferences
) {

    fun getViewPagerLastPage(totalPages: Int) : Int{
        val lastPage = appPrefsUseCase.getViewPagerPodcastLastPage()
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int){
        appPrefsUseCase.setViewPagerPodcastLastPage(page)
    }

    fun getCategories() = appPrefsUseCase
            .getPodcastLibraryCategories()
            .filter { it.visible }

}
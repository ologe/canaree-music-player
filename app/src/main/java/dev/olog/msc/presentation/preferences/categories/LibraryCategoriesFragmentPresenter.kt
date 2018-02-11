package dev.olog.msc.presentation.preferences.categories

import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.domain.interactor.prefs.AppPreferencesUseCase
import javax.inject.Inject

class LibraryCategoriesFragmentPresenter @Inject constructor(
        private val appPreferencesUseCase: AppPreferencesUseCase
){

    fun getDefaultDataSet(): List<LibraryCategoryBehavior>{
        return appPreferencesUseCase.getDefaultLibraryCategories()
    }

    fun getDataSet() : List<LibraryCategoryBehavior> {
        return appPreferencesUseCase.getLibraryCategories()
    }

    fun setDataSet(list: List<LibraryCategoryBehavior>){
        appPreferencesUseCase.setLibraryCategories(list)
    }


}
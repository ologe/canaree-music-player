package dev.olog.presentation.activity_preferences.categories

import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.domain.interactor.prefs.CategoriesBehaviorUseCase
import javax.inject.Inject

class LibraryCategoriesFragmentPresenter @Inject constructor(
        private val categoriesBehaviorUseCase: CategoriesBehaviorUseCase

){

    fun getDefaultDataSet(): List<LibraryCategoryBehavior>{
        return categoriesBehaviorUseCase.getDefault()
    }

    fun getDataSet() = categoriesBehaviorUseCase.get()

    fun setDataSet(list: List<LibraryCategoryBehavior>){
        categoriesBehaviorUseCase.set(list)
    }


}
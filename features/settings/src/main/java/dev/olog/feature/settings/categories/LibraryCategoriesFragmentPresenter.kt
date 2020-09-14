package dev.olog.feature.settings.categories

import dev.olog.domain.MediaIdCategory
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import javax.inject.Inject

internal class LibraryCategoriesFragmentPresenter @Inject constructor(
    private val preferences: CommonPreferences
) {

    fun getDefaultDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior> {
        TODO("remove this")
    }

    fun getDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior> {
        TODO("remove this")
    }

    fun setDataSet(category: MediaIdCategory, list: List<LibraryCategoryBehavior>) {
        TODO("remove this")
    }

}
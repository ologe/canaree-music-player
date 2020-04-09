package dev.olog.presentation.prefs.categories

import dev.olog.domain.MediaIdCategory
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import javax.inject.Inject

internal class LibraryCategoriesFragmentPresenter @Inject constructor(
    private val preferences: CommonPreferences
) {

    fun getDefaultDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior> {
        if (category == MediaIdCategory.PODCASTS) {
            return preferences.getDefaultPodcastLibraryCategories()
        }
        return preferences.getDefaultLibraryCategories()
    }

    fun getDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior> {
        if (category == MediaIdCategory.PODCASTS) {
            return preferences.getPodcastLibraryCategories()
        }
        return preferences.getLibraryCategories()
    }

    fun setDataSet(category: MediaIdCategory, list: List<LibraryCategoryBehavior>) {
        if (category == MediaIdCategory.PODCASTS) {
            preferences.setPodcastLibraryCategories(list)
        } else {
            preferences.setLibraryCategories(list)
        }

    }

}
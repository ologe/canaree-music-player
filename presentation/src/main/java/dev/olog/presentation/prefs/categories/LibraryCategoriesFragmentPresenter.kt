package dev.olog.presentation.prefs.categories

import dev.olog.core.MediaIdCategory
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.LibraryPrefs
import javax.inject.Inject

internal class LibraryCategoriesFragmentPresenter @Inject constructor(
        private val libraryPrefs: LibraryPrefs
){

    fun getDefaultDataSet(category: MediaIdCategory): List<LibraryCategoryBehavior>{
        if (category == MediaIdCategory.PODCASTS){
            return libraryPrefs.getDefaultPodcastLibraryCategories()
        }
        return libraryPrefs.getDefaultLibraryCategories()
    }

    fun getDataSet(category: MediaIdCategory) : List<LibraryCategoryBehavior> {
        if (category == MediaIdCategory.PODCASTS){
            return libraryPrefs.getPodcastLibraryCategories()
        }
        return libraryPrefs.getLibraryCategories()
    }

    fun setDataSet(category: MediaIdCategory, list: List<LibraryCategoryBehavior>){
        if (category == MediaIdCategory.PODCASTS){
            libraryPrefs.setPodcastLibraryCategories(list)
        } else {
            libraryPrefs.setLibraryCategories(list)
        }

    }

}
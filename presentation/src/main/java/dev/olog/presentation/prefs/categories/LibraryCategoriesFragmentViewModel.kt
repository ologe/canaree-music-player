package dev.olog.presentation.prefs.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaIdCategory
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.LibraryPreferences
import javax.inject.Inject

@HiltViewModel
internal class LibraryCategoriesFragmentViewModel @Inject constructor(
        private val libraryPrefs: LibraryPreferences
) : ViewModel() {

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
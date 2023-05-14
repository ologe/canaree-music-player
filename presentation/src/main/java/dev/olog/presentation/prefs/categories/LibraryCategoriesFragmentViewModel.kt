package dev.olog.presentation.prefs.categories

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.presentation.model.PresentationPreferencesGateway
import javax.inject.Inject

@HiltViewModel
internal class LibraryCategoriesFragmentViewModel @Inject constructor(
        private val appPreferencesUseCase: PresentationPreferencesGateway
) : ViewModel() {

    fun getDefaultDataSet(isPodcast: Boolean): List<LibraryCategoryBehavior>{
        if (isPodcast){
            return appPreferencesUseCase.getDefaultPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getDefaultLibraryCategories()
    }

    fun getDataSet(isPodcast: Boolean) : List<LibraryCategoryBehavior> {
        if (isPodcast){
            return appPreferencesUseCase.getPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getLibraryCategories()
    }

    fun setDataSet(isPodcast: Boolean, list: List<LibraryCategoryBehavior>){
        if (isPodcast){
            appPreferencesUseCase.setPodcastLibraryCategories(list)
        } else {
            appPreferencesUseCase.setLibraryCategories(list)
        }

    }

}
package dev.olog.feature.library.library.prefs

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dev.olog.feature.library.library.LibraryFragmentCategoryState
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.navigation.Params
import dev.olog.shared.android.extensions.argument

internal class LibraryPrefsFragmentViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val appPreferencesUseCase: LibraryPreferencesGateway
) : ViewModel() {

    private val isPodcast = state.argument<Boolean>(Params.IS_PODCAST)

    fun getDefaultDataSet(): List<LibraryFragmentCategoryState> {
        if (isPodcast) {
            return appPreferencesUseCase.getDefaultPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getDefaultLibraryCategories()
    }

    fun getDataSet(): List<LibraryFragmentCategoryState> {
        if (isPodcast) {
            return appPreferencesUseCase.getPodcastLibraryCategories()
        }
        return appPreferencesUseCase.getLibraryCategories()
    }

    fun setDataSet(list: List<LibraryFragmentCategoryState>) {
        if (isPodcast) {
            appPreferencesUseCase.setPodcastLibraryCategories(list)
        } else {
            appPreferencesUseCase.setLibraryCategories(list)
        }

    }

}
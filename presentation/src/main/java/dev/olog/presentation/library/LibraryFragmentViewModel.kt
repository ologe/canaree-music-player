package dev.olog.presentation.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.toTabCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
internal class LibraryFragmentViewModel @Inject constructor(
    private val appPrefsUseCase: PresentationPreferencesGateway,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway,
    private val appPreferencesUseCase: SortPreferences,
) : ViewModel() {

    fun stateFlow(page: LibraryPage): Flow<LibraryScreenState> = when (page) {
        LibraryPage.TRACKS -> appPrefsUseCase.getLibraryCategories().map { tabs ->
            val totalPages = tabs.size
            LibraryScreenState(
                libraryPage = page,
                initialTab = appPrefsUseCase.getViewPagerLibraryLastPage().coerceIn(0, totalPages),
                tabs = tabs.map { it.category.toTabCategory() },
                showPodcast = canShowPodcasts(),
            )
        }
        LibraryPage.PODCASTS -> appPrefsUseCase.getPodcastLibraryCategories().map { tabs ->
            val totalPages = tabs.size
            LibraryScreenState(
                libraryPage = page,
                initialTab = appPrefsUseCase.getViewPagerPodcastLastPage().coerceIn(0, totalPages),
                tabs = tabs.map { it.category.toTabCategory() },
                showPodcast = true,
            )
        }
    }

    fun setViewPagerLastPage(index: Int, libraryPage: LibraryPage) {
        when (libraryPage) {
            LibraryPage.TRACKS -> appPrefsUseCase.setViewPagerLibraryLastPage(index)
            LibraryPage.PODCASTS -> appPrefsUseCase.setViewPagerPodcastLastPage(index)
        }
    }

    fun showFloatingWindowTutorialIfNeverShown(): Boolean {
        return tutorialPreferenceUseCase.floatingWindowTutorial()
    }

    fun setLibraryPage(page: LibraryPage) {
        appPrefsUseCase.setLibraryPage(page)
    }

    fun getAllTracksSortOrder(mediaId: MediaId): SortEntity? {
        if (mediaId.isAnyPodcast) {
            return null
        }
        return appPreferencesUseCase.getAllTracksSort()
    }

    // TODO unused
    fun getAllAlbumsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllAlbumsSort()
    }

    // TODO unused
    fun getAllArtistsSortOrder(): SortEntity {
        return appPreferencesUseCase.getAllArtistsSort()
    }

    private fun canShowPodcasts() = appPrefsUseCase.canShowPodcasts()

}
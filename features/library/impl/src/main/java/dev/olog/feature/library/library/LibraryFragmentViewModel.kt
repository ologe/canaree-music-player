package dev.olog.feature.library.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olog.core.MediaStoreType
import dev.olog.core.prefs.TutorialPreferenceGateway
import dev.olog.feature.library.LibraryCategoryBehavior
import dev.olog.feature.library.LibraryPage
import dev.olog.feature.library.LibraryPrefs
import dev.olog.shared.clamp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryFragmentViewModel @Inject constructor(
    private val libraryPrefs: LibraryPrefs,
    private val tutorialPreferenceUseCase: TutorialPreferenceGateway
) : ViewModel() {

    sealed interface Event {
        object ShowFloatingWindowTutorial : Event
        data class ChangePage(val page: LibraryPage) : Event
    }

    private val _events = Channel<Event>()
    val events: Flow<Event> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            delay(500)
            if (tutorialPreferenceUseCase.floatingWindowTutorial()) {
                _events.send(Event.ShowFloatingWindowTutorial)
            }
        }
    }

    fun getViewPagerLastPage(totalPages: Int, type: MediaStoreType): Int {
        val lastPage = when (type) {
            MediaStoreType.Podcast -> libraryPrefs.getViewPagerPodcastLastPage()
            MediaStoreType.Song -> libraryPrefs.getViewPagerLibraryLastPage()
        }
        return clamp(lastPage, 0, totalPages)
    }

    fun setViewPagerLastPage(page: Int, type: MediaStoreType) {
        return when (type) {
            MediaStoreType.Podcast -> libraryPrefs.setViewPagerPodcastLastPage(page)
            MediaStoreType.Song -> libraryPrefs.setViewPagerLibraryLastPage(page)
        }
    }

    fun getCategories(type: MediaStoreType): List<LibraryCategoryBehavior> {
        return when (type) {
            MediaStoreType.Song -> libraryPrefs.getLibraryCategories()
            MediaStoreType.Podcast -> libraryPrefs.getPodcastLibraryCategories()
        }.filter { it.visible }
    }

    fun canShowPodcasts() = libraryPrefs.canShowPodcasts()

    fun updateLibraryPage(page: LibraryPage) {
        libraryPrefs.setLibraryPage(page)
        _events.trySend(Event.ChangePage(page))
    }

}
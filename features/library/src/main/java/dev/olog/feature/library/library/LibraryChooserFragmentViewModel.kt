package dev.olog.feature.library.library

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.feature.presentation.base.prefs.CommonPreferences
import dev.olog.lib.analytics.TrackerFacade
import dev.olog.navigation.Navigator
import dev.olog.navigation.screens.LibraryPage
import kotlinx.coroutines.flow.Flow

internal class LibraryChooserFragmentViewModel @ViewModelInject constructor(
    private val prefs: CommonPreferences,
    private val tracker: TrackerFacade,
    private val navigator: Navigator
) : ViewModel() {

    val libraryPageFlow: Flow<LibraryPage>
        get() = prefs.libraryPageFlow

    var libraryPage: LibraryPage
        get() = prefs.libraryPage
        set(value) {
            prefs.libraryPage = value
    }

    fun trackScreen(page: LibraryPage) {
//        tracker.trackScreen() TODO
    }

    fun toChooseLibrary() {
        navigator.toLibraryChooser()
    }

}
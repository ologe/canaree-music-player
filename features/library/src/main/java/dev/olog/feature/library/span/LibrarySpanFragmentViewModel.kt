package dev.olog.feature.library.span

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.olog.domain.MediaIdCategory
import dev.olog.feature.library.prefs.LibraryPreferences

internal class LibrarySpanFragmentViewModel @ViewModelInject constructor(
    private val prefs: LibraryPreferences
) : ViewModel() {

    fun getSpanCount(category: MediaIdCategory): Int = prefs.getSpanCount(category)

    fun setSpanCount(category: MediaIdCategory, spanCount: Int) {
        prefs.setSpanCount(category, spanCount)
    }

}
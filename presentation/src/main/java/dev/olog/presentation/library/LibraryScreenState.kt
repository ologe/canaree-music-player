package dev.olog.presentation.library

import androidx.compose.runtime.Stable
import dev.olog.presentation.tab.TabCategory

@Stable
data class LibraryScreenState(
    val libraryPage: LibraryPage,
    val initialTab: Int,
    val tabs: List<TabCategory>,
    val showPodcast: Boolean,
)
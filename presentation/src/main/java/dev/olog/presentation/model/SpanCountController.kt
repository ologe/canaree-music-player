package dev.olog.presentation.model

import dev.olog.presentation.tab.TabCategory
import dev.olog.shared.throwNotHandled

internal object SpanCountController {

    const val SPAN_COUNT = 60

    @JvmStatic
    fun getDefaultSpan(category: TabCategory): Int {
        return when (category) {
            TabCategory.FOLDERS -> 3
            TabCategory.PLAYLISTS -> 3
            TabCategory.PODCASTS_PLAYLIST -> 2
            TabCategory.SONGS,
            TabCategory.PODCASTS -> 1
            TabCategory.ALBUMS -> 2
            TabCategory.ARTISTS -> 3
            TabCategory.PODCASTS_AUTHORS -> 2
            TabCategory.GENRES -> 3
            else -> throwNotHandled("invalid $category")
        }
    }

}
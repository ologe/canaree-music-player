package dev.olog.presentation.model

import dev.olog.presentation.tab.TabCategory
import dev.olog.shared.throwNotHandled

internal object SpanCountController {

    const val SPAN_COUNT = 60

    @JvmStatic
    fun getDefaultSpan(category: TabCategory): Int {
        return when (category) {
            TabCategory.FOLDERS -> 3
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> 3
            TabCategory.SONGS,
            TabCategory.PODCASTS -> 1
            TabCategory.ALBUMS -> 2
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_AUTHORS -> 3
            TabCategory.GENRES -> 3
            else -> throwNotHandled("invalid $category")
        }
    }

}
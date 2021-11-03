package dev.olog.feature.tab.layout.manager

import android.content.Context
import dev.olog.feature.library.TabCategory
import dev.olog.shared.android.extensions.configuration

object SpanCountController {

    const val SPAN_COUNT = 60

    @JvmStatic
    fun getDefaultSpan(context: Context, category: TabCategory): Int {
        val smallestWidthDip = context.configuration.smallestScreenWidthDp
        val isTablet = smallestWidthDip >= 600
        return when (category) {
            TabCategory.FOLDERS -> if (isTablet) 4 else 3
            TabCategory.PLAYLISTS,
            TabCategory.PODCASTS_PLAYLIST -> if (isTablet) 4 else 3
            TabCategory.SONGS,
            TabCategory.PODCASTS -> 1
            TabCategory.ALBUMS,
            TabCategory.PODCASTS_ALBUMS -> if (isTablet) 4 else 2
            TabCategory.ARTISTS,
            TabCategory.PODCASTS_ARTISTS -> if (isTablet) 4 else 3
            TabCategory.GENRES -> if (isTablet) 4 else 3
            else -> error("invalid $category")
        }
    }

}
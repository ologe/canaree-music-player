package dev.olog.feature.library.tab.span

import android.content.Context
import dev.olog.feature.library.tab.model.TabFragmentCategory
import dev.olog.shared.android.extensions.configuration

internal object TabFragmentSpanCountController {

    const val SPAN_COUNT = 60

    fun getDefaultSpan(context: Context, category: TabFragmentCategory): Int {
        val smallestWidthDip = context.configuration.smallestScreenWidthDp
        val isTablet = smallestWidthDip >= 600
        return when (category) {
            TabFragmentCategory.FOLDERS -> if (isTablet) 4 else 3
            TabFragmentCategory.PLAYLISTS,
            TabFragmentCategory.PODCASTS_PLAYLIST -> if (isTablet) 4 else 3
            TabFragmentCategory.SONGS,
            TabFragmentCategory.PODCASTS -> 1
            TabFragmentCategory.ALBUMS,
            TabFragmentCategory.PODCASTS_ALBUMS -> if (isTablet) 4 else 2
            TabFragmentCategory.ARTISTS,
            TabFragmentCategory.PODCASTS_ARTISTS -> if (isTablet) 4 else 3
            TabFragmentCategory.GENRES -> if (isTablet) 4 else 3
            else -> error("invalid $category")
        }
    }

}
package dev.olog.presentation.model

import android.content.Context
import dev.olog.core.MediaIdCategory
import dev.olog.shared.android.extensions.configuration

internal object SpanCountController {

    const val SPAN_COUNT = 60

    @JvmStatic
    fun getDefaultSpan(context: Context, category: MediaIdCategory): Int {
        val smallestWidthDip = context.configuration.smallestScreenWidthDp
        val isTablet = smallestWidthDip >= 600
        return when (category) {
            MediaIdCategory.FOLDERS -> if (isTablet) 4 else 3
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> if (isTablet) 4 else 3
            MediaIdCategory.SONGS,
            MediaIdCategory.PODCASTS -> 1
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> if (isTablet) 4 else 2
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> if (isTablet) 4 else 3
            MediaIdCategory.GENRES -> if (isTablet) 4 else 3
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("remove when possible")
        }
    }

}
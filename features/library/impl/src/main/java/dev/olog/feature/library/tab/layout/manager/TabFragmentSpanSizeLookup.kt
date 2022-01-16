package dev.olog.feature.library.tab.layout.manager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import dev.olog.core.MediaUri
import dev.olog.shared.android.extensions.configuration

class TabFragmentSpanSizeLookup(
    var requestedSpanSize: Int,
    private val category: MediaUri.Category,
) : GridLayoutManager.SpanSizeLookup() {

    companion object {

        const val SPAN_COUNT = 60

        fun getDefaultSpanSize(context: Context, category: MediaUri.Category): Int {
            val smallestWidthDip = context.configuration.smallestScreenWidthDp
            val isTablet = smallestWidthDip >= 600
            return when (category) {
                MediaUri.Category.Folder -> if (isTablet) 4 else 3
                MediaUri.Category.Playlist -> if (isTablet) 4 else 3
                MediaUri.Category.Track -> 1
                MediaUri.Category.Collection -> if (isTablet) 4 else 2
                MediaUri.Category.Author -> if (isTablet) 4 else 3
                MediaUri.Category.Genre -> if (isTablet) 4 else 3
            }
        }
    }

    override fun getSpanSize(position: Int): Int {
        return when (category) {
            MediaUri.Category.Author,
            MediaUri.Category.Collection -> getCollectionSpanSize(position)
            MediaUri.Category.Playlist -> getPlaylistSpanSize(position)
            MediaUri.Category.Folder,
            MediaUri.Category.Track,
            MediaUri.Category.Genre -> SPAN_COUNT / requestedSpanSize
        }
    }

    private fun getCollectionSpanSize(position: Int): Int {
        when (position) {
            in 0..4 -> return SPAN_COUNT
        }

        return SPAN_COUNT / requestedSpanSize
    }

    private fun getPlaylistSpanSize(position: Int): Int {
        when (position) {
            0, 4 -> return SPAN_COUNT
        }

        return SPAN_COUNT / requestedSpanSize
    }

}
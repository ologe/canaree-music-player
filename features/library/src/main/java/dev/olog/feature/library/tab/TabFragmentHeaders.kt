package dev.olog.feature.library.tab

import android.content.Context
import android.content.res.Resources
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.DisplayableHeader
import dev.olog.feature.presentation.base.model.PresentationId.Companion.headerId
import javax.inject.Inject

internal class TabFragmentHeaders @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val resources: Resources
        get() = context.resources

    val allPlaylistHeader: DisplayableHeader
        get() = DisplayableHeader(
            R.layout.item_tab_header,
            headerId("all playlist"),
            resources.getString(R.string.tab_all_playlists)
        )

    val autoPlaylistHeader: DisplayableHeader
        get() = DisplayableHeader(
            R.layout.item_tab_header,
            headerId("auto playlist"),
            resources.getString(R.string.tab_auto_playlists)
        )

}
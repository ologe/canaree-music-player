package dev.olog.feature.library.tab.adapter

import android.content.Context
import android.view.View
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.AdapterWithHeader
import dev.olog.feature.base.adapter.MediaListItemAdapter
import dev.olog.feature.base.adapter.TextHeaderAdapter
import dev.olog.feature.base.adapter.media.ItemDirection
import dev.olog.feature.base.adapter.media.MediaListItem
import localization.R

class TabFragmentAutoPlaylistAdapter(
    context: Context,
    onItemClick: (MediaUri) -> Unit,
    onItemLongClick: (MediaUri, View) -> Unit,
) : AdapterWithHeader<MediaListItemAdapter, MediaListItem>(
    TextHeaderAdapter(context.getString(R.string.tab_auto_playlists)),
    MediaListItemAdapter(
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick,
        direction = ItemDirection.Horizontal(context),
    )
)
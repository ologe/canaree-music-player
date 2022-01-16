package dev.olog.feature.library.tab.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.media.ItemDirection
import dev.olog.feature.base.adapter.media.MediaListItem
import localization.R

class TabFragmentPlaylistAdapter(
    context: Context,
    onItemClick: (MediaUri) -> Unit,
    onItemLongClick: (MediaUri, View) -> Unit,
) : AdapterWithHeader<MediaListItemAdapter, MediaListItem>(
    TextHeaderAdapter(context.getString(R.string.tab_all_playlists)),
    MediaListItemAdapter(
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick,
        direction = ItemDirection.Horizontal(context),
    )
)
package dev.olog.feature.library.tab.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.AdapterWithHeader
import dev.olog.feature.base.adapter.NestedHorizontalAdapter
import dev.olog.feature.base.adapter.TextHeaderAdapter
import dev.olog.feature.base.adapter.horizontal
import dev.olog.feature.base.adapter.media.ItemDirection
import dev.olog.feature.base.adapter.media.MediaListItem
import dev.olog.feature.base.adapter.media.MediaListItemAdapter
import localization.R

class TabFragmentRecentlyPlayedAdapter(
    context: Context,
    onItemClick: (MediaUri) -> Unit,
    onItemLongClick: (MediaUri, View) -> Unit,
) : AdapterWithHeader<NestedHorizontalAdapter<*, MediaListItem>, RecyclerView.ViewHolder, MediaListItem>(
    headerAdapter = TextHeaderAdapter(context.getString(R.string.tab_recent_played)),
    adapter = MediaListItemAdapter(
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick,
        direction = ItemDirection.Horizontal(context),
    ).horizontal(context),
)
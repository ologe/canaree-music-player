package dev.olog.feature.library.tab.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.*
import dev.olog.feature.base.adapter.media.ItemDirection
import dev.olog.feature.base.adapter.media.MediaListItem
import localization.R

class TabFragmentRecentlyAddedAdapter(
    context: Context,
    onItemClick: (MediaUri) -> Unit,
    onItemLongClick: (MediaUri, View) -> Unit,
) : AdapterWithHeader<NestedHorizontalAdapter<MediaListItem, *>, MediaListItem>(
    headerAdapter = TextHeaderAdapter(context.getString(R.string.common_recently_added)),
    adapter = MediaListItemAdapter(
        onItemClick = onItemClick,
        onItemLongClick = onItemLongClick,
        direction = ItemDirection.Horizontal(context),
    ).horizontal()
)
package dev.olog.feature.library.tab.adapter

import android.view.View
import dev.olog.core.MediaUri
import dev.olog.feature.base.adapter.media.MediaListItemAdapter

class TabFragmentMediaAdapter(
    onItemClick: (MediaUri) -> Unit,
    onItemLongClick: (MediaUri, View) -> Unit,
) : MediaListItemAdapter(onItemClick, onItemLongClick)
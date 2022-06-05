package dev.olog.feature.search.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.olog.compose.CanareeIconButton
import dev.olog.compose.CanareeIcons
import dev.olog.compose.gesture.CircularSwipeToDismiss
import dev.olog.compose.gesture.clickable
import dev.olog.compose.text.Footer
import dev.olog.compose.text.Header
import dev.olog.core.MediaId
import dev.olog.feature.library.api.widget.MediaTrack
import dev.olog.feature.search.model.SearchState

@Suppress("FunctionName")
fun LazyListScope.SearchRecentList(
    data: SearchState.Recents,
    onPlayableClick: (MediaId) -> Unit,
    onNonPlayableClick: (MediaId) -> Unit,
    onItemLongClick: (MediaId) -> Unit,
    onClearItemClick: (MediaId) -> Unit,
    onClearAllClick: () -> Unit,
    onPlayNext: (MediaId) -> Unit,
) {
    item {
        Header(stringResource(id = localization.R.string.search_recent_searches))
    }

    println("xx ${data.items.map { it.mediaId }}")
    items(
        items = data.items,
        key = { it.mediaId }
    ) { item ->
        CircularSwipeToDismiss(
            directions = if (item.isPlayable) setOf(EndToStart, StartToEnd) else setOf(StartToEnd),
            onDelete = { onClearItemClick(item.mediaId); false },
            onPlayNext = { onPlayNext(item.mediaId); true }
        ) {
            MediaTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            if (item.isPlayable) {
                                onPlayableClick(item.mediaId)
                            } else {
                                onNonPlayableClick(item.mediaId)
                            }
                        },
                        onLongClick = {
                            onItemLongClick(item.mediaId)
                        }
                    ),
            ) {
                CanareeIconButton(
                    imageVector = CanareeIcons.Clear,
                    onClick = { onClearItemClick(item.mediaId) }
                )
            }
        }
    }

    item {
        Footer(
            text = stringResource(localization.R.string.search_clear_recent_searches),
            modifier = Modifier.clickable { onClearAllClick() }
        )
    }
}
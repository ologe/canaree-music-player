package dev.olog.presentation.detail.adapter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun DetailMostPlayedContent(
    mostPlayed: DetailFragmentItem.MostPlayed,
    onClick: (MediaId) -> Unit,
    onLongClick: (MediaId) -> Unit,
) {
    if (mostPlayed.items.isEmpty()) {
        return
    }

    Column(Modifier.fillMaxWidth()) {
        ListItemHeader(stringResource(R.string.detail_most_played))

        DetailLazyHorizontalGrid(mostPlayed.items) { item ->
            ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onClick(item.mediaId) },
                onLongClick = { onLongClick(item.mediaId) },
                position = item.position,
                trailingContent = {
                    IconButton(
                        drawableRes = R.drawable.vd_more,
                        onClick = { onLongClick(item.mediaId) }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.background)
        ) {
            DetailMostPlayedContent(
                DetailFragmentItem.MostPlayed(
                    items = (0..10).map {
                        DetailMostPlayedItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                            position = (it + 1).toString(),
                        )
                    },
                ),
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
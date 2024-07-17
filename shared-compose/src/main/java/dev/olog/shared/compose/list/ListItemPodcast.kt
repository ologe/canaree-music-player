package dev.olog.shared.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.shaped
import dev.olog.shared.compose.glide.BindingAdapters
import dev.olog.shared.compose.list.internal.ListItemRow

@Composable
fun ListItemPodcast(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    duration: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItemRow(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = subtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = duration,
                    color = Theme.accentColor,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        leadingContent = {
            AsyncImage(Modifier.shaped(mediaId)) {
                BindingAdapters.loadSongImage(this, mediaId)
            }
        },
        trailingContent = null,
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(MaterialTheme.colors.background)) {
            ListItemPodcast(
                mediaId = MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                duration = "56m",
                onClick = {},
                onLongClick = {},
            )
            ListItemPodcast(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1"),
                title = "Angel Beach",
                subtitle = "Trilogy",
                duration = "56m",
                onClick = {},
                onLongClick = {},
            )
            val lorem = LoremIpsum(20).values.joinToString()
            ListItemPodcast(
                mediaId = MediaId.songId(1),
                title = lorem,
                subtitle = lorem,
                duration = "56m",
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
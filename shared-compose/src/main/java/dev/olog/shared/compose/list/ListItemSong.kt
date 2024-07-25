package dev.olog.shared.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.Explicit
import dev.olog.shared.compose.component.shaped
import dev.olog.shared.compose.glide.BindingAdapters
import dev.olog.shared.compose.list.internal.ListItemRow

@Composable
fun ListItemSong(
    mediaId: MediaId,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    indexContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItemSong(
        leadingContent = {
            AsyncImage(Modifier.shaped(mediaId)) {
                BindingAdapters.loadSongImage(this, mediaId)
            }
        },
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        trailingContent = trailingContent,
        indexContent = indexContent,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

@Composable
fun ListItemSong(
    leadingContent: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    indexContent: (@Composable () -> Unit)? = null,
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
        subtitle = subtitle?.let{ {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                indexContent?.let {
                    Index(content = it)
                }
                Explicit(title = title)
                Text(
                    text = subtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }},
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Composable
private fun Index(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .background(Theme.textColorPrimary, RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides (if (isSystemInDarkTheme()) Color.Black else Color.White),
            LocalTextStyle provides LocalTextStyle.current.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            ),
            content = content,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(MaterialTheme.colors.background)) {
            ListItemSong(
                mediaId = MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                onClick = {},
                onLongClick = {},
            )
            ListItemSong(
                mediaId = MediaId.songId(1),
                title = "Angel Beach",
                subtitle = null,
                onClick = {},
                onLongClick = {},
            )
            ListItemSong(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1"),
                title = "Angel Beach",
                subtitle = "Trilogy",
                onClick = {},
                onLongClick = {},
            )
            ListItemSong(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1"),
                title = "Angel Beach",
                subtitle = "Trilogy",
                indexContent = { Text(text = "+100") },
                onClick = {},
                onLongClick = {},
            )
            ListItemSong(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1"),
                title = "Angel Beach",
                subtitle = "Trilogy",
                trailingContent = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Rounded.DragHandle,
                            contentDescription = null,
                        )
                    }
                },
                onClick = {},
                onLongClick = {},
            )
            val lorem = LoremIpsum(20).values.joinToString()
            ListItemSong(
                mediaId = MediaId.songId(1),
                title = lorem,
                subtitle = lorem,
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
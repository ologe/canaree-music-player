package dev.olog.shared.compose.list

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.QuickAction
import dev.olog.shared.compose.component.shaped
import dev.olog.shared.compose.glide.BindingAdapters
import dev.olog.shared.compose.list.internal.ListItemColumn

// TODO try to implement colored ripple
@Composable
fun ListItemAlbum(
    mediaId: MediaId,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    ListItemColumn(
        image = {
            Image(mediaId, interactionSource)
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitle = subtitle?.let { {
            Text(
                text = it,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } },
        interactionSource = interactionSource,
        modifier = modifier,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Composable
private fun Image(
    mediaId: MediaId,
    interactionSource: InteractionSource,
) {
    Box {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .shaped(mediaId)
                .indication(
                    interactionSource = interactionSource,
                    indication = LocalIndication.current,
                ),
            update = { BindingAdapters.loadAlbumImage(this, mediaId) }
        )
        QuickAction(
            mediaId = mediaId,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(MaterialTheme.colors.background)) {
            ListItemAlbum(
                mediaId = MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                modifier = Modifier.width(150.dp),
                onClick = {},
                onLongClick = {},
            )
            ListItemAlbum(
                mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "1"),
                title = "Angel Beach",
                subtitle = "Trilogy",
                modifier = Modifier.width(150.dp),
                onClick = {},
                onLongClick = {},
            )
            val lorem = LoremIpsum(20).values.joinToString()
            ListItemAlbum(
                mediaId = MediaId.songId(1),
                title = lorem,
                subtitle = lorem,
                modifier = Modifier
                    .width(150.dp),
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
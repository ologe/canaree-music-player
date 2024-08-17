package dev.olog.shared.compose.list

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.shaped
import dev.olog.shared.compose.glide.BindingAdapters
import dev.olog.shared.compose.list.internal.ListItemColumn
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme

@Composable
fun ListItemBlacklist(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    isBlacklisted: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    ListItemColumn(
        image = {
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
            if (isBlacklisted) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x88_000000), RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.vd_lock),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF_FAFAFA)
                    )
                }
            }
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
        onLongClick = null,
    )
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            ListItemBlacklist(
                mediaId = MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                isBlacklisted = false,
                modifier = Modifier.width(150.dp),
                onClick = {},
            )
            ListItemBlacklist(
                mediaId = MediaId.songId(2),
                title = "Angel Beach",
                subtitle = "Trilogy",
                isBlacklisted = true,
                modifier = Modifier.width(150.dp),
                onClick = {},
            )
        }
    }
}
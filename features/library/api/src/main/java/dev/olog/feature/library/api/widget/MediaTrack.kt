package dev.olog.feature.library.api.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.compose.components.CanareeBackground
import dev.olog.compose.components.CanareeIconButton
import dev.olog.compose.CanareeIcons
import dev.olog.compose.ThemePreviews
import dev.olog.compose.WithMediumEmphasys
import dev.olog.compose.gesture.scalableContent
import dev.olog.compose.components.CanareeImage
import dev.olog.compose.composition.local.LocalImageShape
import dev.olog.compose.composition.local.toComposeShape
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.platform.theme.ImageShape

@Composable
fun MediaTrack(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    shape: Shape = if (mediaId.isAnyArtist) CircleShape else LocalImageShape.current.toComposeShape(),
    endContent: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .scalableContent(.97f)
            .padding(
                start = 16.dp,
                end = if (endContent == null) 16.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CanareeImage(
            mediaId = mediaId,
            modifier = Modifier
                .padding(vertical = 9.dp)
                .size(55.dp)
                .clip(shape)
        )

        val isExplicit = remember(title) { // todo check for performance
            title.contains("explicit", ignoreCase = true)
        }

        // use a spacer instead of horizontalArrangement to avoid space
        // between text and endContent
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isExplicit) {
                    Icon(
                        imageVector = CanareeIcons.Explicit,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }

                WithMediumEmphasys {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        endContent?.invoke(this)
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground {
            Column {
                val lorem = LoremIpsum(7)
                for ((index, value) in ImageShape.values().withIndex()) {
                    MediaTrack(
                        mediaId = MediaId.songId(index.toLong()),
                        title = lorem.values.joinToString(),
                        subtitle = lorem.values.joinToString(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = value.toComposeShape(),
                    )
                }

                MediaTrack(
                    mediaId = MediaId.songId(1),
                    title = "Short title",
                    subtitle = "Short subtitle",
                    modifier = Modifier.fillMaxWidth(),
                )

                MediaTrack(
                    mediaId = MediaId.songId(1),
                    title = "Lorem Ipsum is simply dummy text of the printing and typesetting industry",
                    subtitle = "Lorem Ipsum is simply dummy text of the printing and typesetting industry",
                    modifier = Modifier.fillMaxWidth(),
                )

                MediaTrack(
                    mediaId = MediaId.songId(1),
                    title = "This song is explicit",
                    subtitle = "Lorem Ipsum is simply dummy text of the printing and typesetting industry",
                    modifier = Modifier.fillMaxWidth(),
                )

                MediaTrack(
                    mediaId = MediaId.songId(1),
                    title = "This song has an icon",
                    subtitle = "Lorem Ipsum is simply dummy text of the printing and typesetting industry",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CanareeIconButton(
                        imageVector = CanareeIcons.MoreVert,
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}
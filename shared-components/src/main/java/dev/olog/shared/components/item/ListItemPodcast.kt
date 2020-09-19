package dev.olog.shared.components.item

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.ProgressBar
import dev.olog.shared.components.SingleLineText
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.image.GlideView
import dev.olog.shared.components.sample.ImageShapePreviewProvider
import dev.olog.shared.components.theme.CanareeTheme

@Preview(heightDp = 100)
@Composable
private fun ListItemPodcastImageShapePreview(
    @PreviewParameter(ImageShapePreviewProvider::class) shapeOverride: ImageShape,
) {
    CanareeTheme(shapeOverride = shapeOverride) {
        Surface {
            val mediaId = MediaId.Track(MediaIdCategory.SONGS, "", "1")
            ListItemPodcast(mediaId, "3 Doors Down", "Kryptonite")
        }
    }
}

@Composable
fun ListItemPodcast(
    mediaId: MediaId.Track,
    title: String,
    subtitle: String,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    onClick: ((MediaId) -> Unit) = {},
    onLongClick: ((MediaId) -> Unit) = {},
    endIcon: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onClick(mediaId) },
                onLongClick = { onLongClick(mediaId) }
            )
            .padding(
                start = startPadding,
                end = endPadding,
                top = 8.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideView(
            mediaId = mediaId,
            modifier = Modifier.preferredWidth(52.dp)
                .aspectRatio(0.85f)
        )
        Column(
            modifier = Modifier.weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.aligned(Alignment.CenterVertically)
        ) {
            SingleLineText(title)
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                SingleLineText(subtitle, style = MaterialTheme.typography.body2)
            }
            Spacer(modifier = Modifier.preferredHeight(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressBar(
                    progress = 0.4f,
                    modifier = Modifier.weight(1f)
                        .padding(end = 16.dp)
                )
                ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                    Text("40%", style = MaterialTheme.typography.body2)
                }
            }
        }
        endIcon?.invoke(this)
    }
}


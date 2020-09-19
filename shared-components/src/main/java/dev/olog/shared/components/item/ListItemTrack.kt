package dev.olog.shared.components.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.SingleLineText
import dev.olog.shared.components.ambient.CustomTheming
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.image.GlideView
import dev.olog.shared.components.sample.ImageShapePreviewProvider
import dev.olog.shared.components.theme.CanareeTheme

//@Preview(heightDp = 68)
//@Preview(heightDp = 68, TODO uiMode = DARK MODE YES) in 4.2 canary 10 seems not to work
//@Composable
//private fun ListItemTrackDarkThemePreview() {
//    CanareeTheme() {
//        Surface {
//            val mediaId = MediaId.Track(MediaIdCategory.SONGS, "", "1")
//            ListItemTrack(mediaId, "3 Doors Down", "Kryptonite")
//        }
//    }
//}

@Preview(heightDp = 68)
@Composable
private fun ListItemTrackImageShapePreview(
    @PreviewParameter(ImageShapePreviewProvider::class) shapeOverride: ImageShape,
) {
    CanareeTheme(shapeOverride = shapeOverride) {
        Surface {
            val mediaId = MediaId.Track(MediaIdCategory.SONGS, "", "1")
            ListItemTrack(mediaId, "3 Doors Down", "Kryptonite")
        }
    }
}

@Composable
fun ListItemTrack(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    shape: Shape = CustomTheming.imageShape,
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
            modifier = Modifier.preferredSize(52.dp),
            shape = shape
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
        }
        endIcon?.invoke(this)
    }
}


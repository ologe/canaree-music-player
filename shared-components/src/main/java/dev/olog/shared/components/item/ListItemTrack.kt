package dev.olog.shared.components.item

import android.content.res.Configuration
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.UiMode
import androidx.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.ambient.ImageShape
import dev.olog.shared.components.image.GlideView
import dev.olog.shared.components.sample.ImageShapePreviewProvider
import dev.olog.shared.components.sample.PerviewProviders
import dev.olog.shared.components.theme.CanareeTheme
import kotlin.random.Random

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
    @PreviewParameter(ImageShapePreviewProvider::class) initialShape: ImageShape,
) {
    CanareeTheme(initialShape = initialShape) {
        Surface {
            val mediaId = MediaId.Track(MediaIdCategory.SONGS, "", "1")
            ListItemTrack(mediaId, "3 Doors Down", "Kryptonite")
        }
    }
}

@Composable
fun ListItemTrack(
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
        verticalGravity = Alignment.CenterVertically
    ) {
        GlideView(
            mediaId = mediaId,
            modifier = Modifier.preferredSize(52.dp)
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

@Composable
fun SingleLineText(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign? = null,
    maxLines: Int = 1,
    style: TextStyle = MaterialTheme.typography.body1
) {
    Text(
        text = text,
        style = style,
        textAlign = align,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.fillMaxWidth().then(modifier)
    )
}
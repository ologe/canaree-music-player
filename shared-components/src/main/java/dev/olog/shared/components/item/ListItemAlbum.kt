package dev.olog.shared.components.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.image.GlideView
import dev.olog.shared.components.sample.MediaIdCategoryPreviewProvider
import dev.olog.shared.components.theme.CanareeTheme

@Preview(widthDp = 200)
@Composable
private fun ListItemAlbumPreview(
    @PreviewParameter(MediaIdCategoryPreviewProvider::class) category: MediaIdCategory
) {
    CanareeTheme {
        Surface {
            val mediaId = MediaId.Category(category, "test")
            ListItemAlbum(mediaId, "Get Rich or Die Tryin", "50 Cnet")
        }
    }
}

@Composable
fun ListItemAlbum(
    mediaId: MediaId.Category,
    title: String,
    subtitle: String
) {


    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
    ) {
        GlideView(
            mediaId = mediaId,
            modifier = Modifier.fillMaxWidth()
        )
        SingleLineText(
            text = title,
            maxLines = 2,
            align = TextAlign.Center
        )
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            SingleLineText(
                subtitle,
                style = MaterialTheme.typography.body2,
                align = TextAlign.Center
            )
        }
    }
}
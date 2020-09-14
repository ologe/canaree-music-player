package dev.olog.shared.components.item

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.shared.components.theme.CanareeTheme

@Preview(widthDp = 200)
@Composable
private fun ListItemAlbumCategoryPreview() {
    CanareeTheme {
        Surface {
            val mediaId = MediaId.Category(MediaIdCategory.ARTISTS, "test")
            ListItemArtist(mediaId, "50 Cent", "10 songs")
        }
    }
}

@Composable
fun ListItemArtist(
    mediaId: MediaId.Category,
    title: String,
    subtitle: String,
) {
    ListItemAlbum(mediaId, title, subtitle, CircleShape)
}
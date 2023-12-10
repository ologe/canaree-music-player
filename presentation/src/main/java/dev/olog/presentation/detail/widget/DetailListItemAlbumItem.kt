package dev.olog.presentation.detail.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.listitem.ListItemTrackWithoutImage
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun DetailListItemAlbumItem(
    trackNumber: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItemTrackWithoutImage(
        trackNumber = trackNumber,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailListItemAlbumItem(
                trackNumber = "1",
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
package dev.olog.presentation.detail.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olog.core.MediaId
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.presentation.R

@Composable
fun DetailListItemFolderItem(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    trackNumber: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItemTrack(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        onLongClick = onLongClick,
        position = trackNumber,
        trailingContent = {
            IconButton(
                drawableRes = R.drawable.vd_more,
                onClick = onLongClick,
            )
        }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailListItemFolderItem(
                mediaId = MediaId.songId(1),
                title = "title",
                subtitle = "subtitle",
                trackNumber = "1",
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
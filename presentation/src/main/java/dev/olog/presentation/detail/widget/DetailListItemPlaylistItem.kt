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
import dev.olog.shared.compose.component.onActionDown

@Composable
fun DetailListItemPlaylistItem(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onStartDrag: () -> Unit,
) {
    ListItemTrack(
        mediaId = mediaId,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        onLongClick = onLongClick,
        trailingContent = {
            IconButton(
                drawableRes = R.drawable.vd_drag_handle,
                modifier = Modifier.onActionDown(onStartDrag)
            )
        }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailListItemPlaylistItem(
                mediaId = MediaId.songId(1),
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {},
                onStartDrag = {}
            )
        }
    }
}
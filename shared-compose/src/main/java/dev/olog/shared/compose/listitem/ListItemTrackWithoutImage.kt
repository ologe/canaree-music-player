package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.scaleDownOnTouch
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.toFakeSp
import dev.olog.shared.compose.R

@Composable
fun ListItemTrackWithoutImage(
    trackNumber: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    ListItemSlots(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .scaleDownOnTouch()
            .padding(contentPadding),
        iconContent = {
            Text(
                text = trackNumber,
                fontSize = 16.dp.toFakeSp(),
                fontWeight = FontWeight.Bold,
            )
        },
        titleContent = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        subtitleContent = {
            Text(
                text = subtitle,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = trailingContent,
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            ListItemTrackWithoutImage(
                trackNumber = "-",
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {}
            )
            ListItemTrackWithoutImage(
                trackNumber = "9",
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {}
            )
            ListItemTrackWithoutImage(
                trackNumber = "99",
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {}
            )
            ListItemTrackWithoutImage(
                trackNumber = "99",
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {},
                trailingContent = {
                    IconButton(drawableRes = R.drawable.vd_more) {

                    }
                }
            )
        }
    }
}
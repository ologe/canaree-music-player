package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.olog.core.MediaId
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Divider
import dev.olog.shared.compose.component.Icon
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.toFakeSp

@Composable
fun ListItemShuffle(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        ListItemSlots(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(paddingValues),
            leadingContent = {
                Icon(painter = painterResource(R.drawable.vd_shuffle),)
            },
            titleContent = {
                Text(
                    text = stringResource(R.string.common_shuffle),
                    style = Theme.typography.body,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dimensionResource(R.dimen.item_shuffle_text_size).toFakeSp(),
                    maxLines = 1,
                )
            },
            subtitleContent = null,
        )

        Divider(
            Modifier
                .padding(horizontal = dimensionResource(R.dimen.item_song_cover_margin_start))
                .padding(paddingValues)
        )
        Spacer(modifier = Modifier.padding(Theme.spacing.extraSmall))
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier.background(Theme.colors.background)
        ) {
            ListItemTrack(
                mediaId = MediaId.songId(1),
                title = "title",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {},
            )
            ListItemTrack(
                mediaId = MediaId.songId(1),
                title = "title (explicit)",
                subtitle = "subtitle",
                onClick = {},
                onLongClick = {},
            )
        }
    }
}
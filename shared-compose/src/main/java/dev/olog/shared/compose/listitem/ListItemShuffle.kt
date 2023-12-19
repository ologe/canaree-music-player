package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Divider
import dev.olog.shared.compose.component.Icon
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.scaleDownOnTouch
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.toFakeSp

@Composable
fun ListItemShuffle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        ListItemSlots(
            modifier = Modifier
                .clip(ListItemSlotsRoundedCorners)
                .clickable(onClick = onClick)
                .scaleDownOnTouch(),
            iconContent = {
                Icon(painter = painterResource(R.drawable.vd_shuffle))
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
                .padding(horizontal = Theme.spacing.medium)
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            ListItemShuffle {}
            ListItemShuffle {}
        }
    }
}
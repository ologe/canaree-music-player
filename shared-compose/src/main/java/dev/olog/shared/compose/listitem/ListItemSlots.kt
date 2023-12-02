package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.R
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalContentColor
import dev.olog.shared.compose.theme.LocalTextStyle
import dev.olog.shared.compose.theme.Theme

@Composable
internal fun ListItemSlots(
    modifier: Modifier,
    leadingContent: @Composable BoxScope.() -> Unit,
    titleContent: @Composable () -> Unit,
    subtitleContent: @Composable (RowScope.() -> Unit)?,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.item_song_cover_margin_vertical))
                .padding(start = dimensionResource(R.dimen.item_song_cover_margin_start))
                .size(dimensionResource(R.dimen.item_song_cover_size)),
            contentAlignment = Alignment.Center,
            content = leadingContent,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = Theme.spacing.medium)
                .weight(1f)
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides Theme.typography.trackTitle,
                LocalContentColor provides Theme.colors.textColorPrimary,
            ) {
                titleContent()
            }


            if (subtitleContent != null) {
                Spacer(modifier = Modifier.padding(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides Theme.typography.trackSubtitle,
                        LocalContentColor provides Theme.colors.textColorSecondary
                    ) {
                        subtitleContent()
                    }
                }
            }
        }

        trailingContent?.invoke()
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            ListItemSlots(
                modifier = Modifier,
                leadingContent = {
                    Spacer(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Theme.colors.textColorPrimary.enabled)
                    )
                },
                titleContent = {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Theme.colors.textColorPrimary.enabled)
                    )
                },
                subtitleContent = {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(Theme.colors.textColorPrimary.enabled)
                    )
                },
                trailingContent = {
                    Spacer(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Theme.colors.textColorPrimary.enabled)
                    )
                }
            )
        }
    }
}
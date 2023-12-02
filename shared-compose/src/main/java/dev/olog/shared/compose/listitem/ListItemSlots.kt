package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
    iconContent: @Composable BoxScope.() -> Unit,
    titleContent: @Composable () -> Unit,
    subtitleContent: @Composable (RowScope.() -> Unit)?,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(start = dimensionResource(R.dimen.item_song_cover_margin_start)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingContent != null) {
            Box(Modifier.padding(horizontal = Theme.spacing.extraSmall)) {
                leadingContent.invoke()
            }
        }

        Box(
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.item_song_cover_margin_vertical))
                .size(dimensionResource(R.dimen.item_song_cover_size)),
            contentAlignment = Alignment.Center,
            content = iconContent,
        )

        Column(
            modifier = Modifier
                .padding(horizontal = Theme.spacing.mediumSmall)
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
        Column(Modifier.background(Theme.colors.background)) {
            val iconContent: @Composable BoxScope.() -> Unit = {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Theme.colors.textColorPrimary.enabled)
                )
            }
            val titleContent: @Composable () -> Unit = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Theme.colors.textColorPrimary.enabled)
                )
            }
            val subtitleContent: @Composable RowScope.() -> Unit = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Theme.colors.textColorPrimary.enabled)
                )
            }
            val leadingContent: @Composable () -> Unit = {
                Spacer(
                    modifier = Modifier
                        .width(12.dp)
                        .fillMaxHeight()
                        .background(Theme.colors.textColorPrimary.enabled)
                )
            }
            val trailingContent: @Composable () -> Unit = {
                Spacer(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Theme.colors.textColorPrimary.enabled)
                )
            }

            ListItemSlots(
                modifier = Modifier,
                iconContent = iconContent,
                titleContent = titleContent,
                subtitleContent = subtitleContent,
            )
            ListItemSlots(
                modifier = Modifier,
                iconContent = iconContent,
                titleContent = titleContent,
                subtitleContent = subtitleContent,
                trailingContent = trailingContent,
            )
            ListItemSlots(
                modifier = Modifier,
                iconContent = iconContent,
                titleContent = titleContent,
                subtitleContent = subtitleContent,
                leadingContent = leadingContent,
                trailingContent = trailingContent,
            )
        }
    }
}
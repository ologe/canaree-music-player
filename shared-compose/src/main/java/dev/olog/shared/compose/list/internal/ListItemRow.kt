@file:OptIn(ExperimentalFoundationApi::class)

package dev.olog.shared.compose.list.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.Capsule
import dev.olog.shared.compose.component.FixedSizeLayout
import dev.olog.shared.compose.component.rememberScaleIndication

@Composable
internal fun ListItemRow(
    title: @Composable () -> Unit,
    subtitle: (@Composable () -> Unit)?,
    leadingContent: @Composable () -> Unit,
    trailingContent: (@Composable () -> Unit)?,
    modifier: Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .indication(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = rememberScaleIndication(),
                interactionSource = interactionSource,
            )
            .padding(
                end = if (trailingContent == null) dimensionResource(R.dimen.item_song_cover_margin_end) else 4.dp,
            )
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FixedSizeLayout(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.item_song_cover_margin_start),
                    top = dimensionResource(R.dimen.item_song_cover_margin_vertical),
                    bottom = dimensionResource(R.dimen.item_song_cover_margin_vertical),
                )
                .size(dimensionResource(R.dimen.item_song_cover_size)),
            content = leadingContent
        )
        Column(
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.item_song_cover_margin_vertical))
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides Theme.colors.textColorPrimary,
                LocalTextStyle provides LocalTextStyle.current.copy(
                    fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_song_title).toSp() },
                ),
                content = title,
            )
            subtitle?.let {
                CompositionLocalProvider(
                    LocalContentColor provides Theme.colors.textColorSecondary,
                    LocalTextStyle provides LocalTextStyle.current.copy(
                        fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_song_subtitle).toSp() },
                    ),
                    content = subtitle
                )
            }
        }
        trailingContent?.let {
            Box(Modifier.size(48.dp)) { // todo min interactive size
                it()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column {
            ListItemRow(
                title = { Capsule(Modifier.fillMaxSize()) },
                subtitle = { Capsule(Modifier.fillMaxSize()) },
                leadingContent = { Capsule(Modifier.fillMaxSize()) },
                trailingContent = null,
                modifier = Modifier,
                onClick = {},
                onLongClick = {}
            )
            ListItemRow(
                title = { Capsule(Modifier.fillMaxSize()) },
                subtitle = { Capsule(Modifier.fillMaxSize()) },
                leadingContent = { Capsule(Modifier.fillMaxSize()) },
                trailingContent = { Capsule(Modifier.fillMaxSize()) },
                modifier = Modifier,
                onClick = {},
                onLongClick = {}
            )
        }
    }
}
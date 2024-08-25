package dev.olog.presentation.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olog.core.entity.sort.SortArranging
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.R
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.list.ListItemHeader

@Composable
fun DetailRelatedArtistsHeader(
    showSeeAll: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ListItemHeader(
        title = stringResource(R.string.detail_related_artists),
        modifier = modifier,
        trailingContent = if (showSeeAll) { {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                )
            }
        } } else null
    )
}

@Composable
fun DetailRecentlyAddedHeader(
    itemsCount: Int,
    showSeeAll: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ListItemHeader(
        title = stringResource(R.string.detail_recently_added),
        subtitle = pluralStringResource(R.plurals.detail_xx_new_songs, itemsCount, itemsCount),
        modifier = modifier,
        trailingContent = if (showSeeAll) { {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                )
            }
        } } else null
    )
}

@Composable
fun DetailSongsHeader(
    sort: SortEntity,
    modifier: Modifier = Modifier,
    onSortClick: () -> Unit,
    onSortDirectionClick: () -> Unit,
) {
    ListItemHeader(
        title = stringResource(R.string.detail_tracks),
        modifier = modifier,
        trailingContent = {
            Text(
                text = stringResource(R.string.common_sort_by).lowercase(),
                fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_header_sec_text_size).toSp() },
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .clickable(
                        indication = rememberRipple(bounded = false),
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onSortClick,
                    ),
                color = Theme.m3Colors.primary,
            )
            IconButton(onClick = onSortDirectionClick) {
                Icon(
                    imageVector = when {
                        sort.type == SortType.CUSTOM -> Icons.Rounded.Remove
                        sort.arranging == SortArranging.ASCENDING -> Icons.Rounded.KeyboardArrowDown
                        sort.arranging == SortArranging.DESCENDING -> Icons.Rounded.KeyboardArrowUp
                        else -> error("invalid $sort")
                    },
                    contentDescription = null,
                    tint = Theme.m3Colors.primary,
                )
            }
        }
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            DetailRelatedArtistsHeader(true) {}
            DetailRelatedArtistsHeader(false) {}
            DetailRecentlyAddedHeader(10, true) {}
            DetailRecentlyAddedHeader(10, false) {}
            DetailSongsHeader(
                sort = SortEntity(SortType.CUSTOM, SortArranging.ASCENDING),
                onSortClick = {},
                onSortDirectionClick = {}
            )
            DetailSongsHeader(
                sort = SortEntity(SortType.TITLE, SortArranging.ASCENDING),
                onSortClick = {},
                onSortDirectionClick = {}
            )
            DetailSongsHeader(
                sort = SortEntity(SortType.TITLE, SortArranging.DESCENDING),
                onSortClick = {},
                onSortDirectionClick = {}
            )
        }
    }
}
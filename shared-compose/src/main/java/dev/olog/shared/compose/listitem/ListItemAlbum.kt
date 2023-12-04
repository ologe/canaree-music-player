package dev.olog.shared.compose.listitem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.placeholder
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.CoverUtils
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.android.theme.QuickAction
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.dynamicShape
import dev.olog.shared.compose.component.scaleDownOnTouch
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalThemeSettings
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.ThemeSettingsOverride

@Composable
fun ListItemAlbum(
    mediaId: MediaId,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.item_album_margin))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            )
            .scaleDownOnTouch(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            AsyncImage(
                mediaId = mediaId,
                modifier = Modifier
                    .aspectRatio(1f)
                    .dynamicShape(mediaId),
            )
            QuickAction(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            )
        }
        Text(
            text = title,
            style = Theme.typography.albumTitle,
            modifier = Modifier.padding(top = 6.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = Theme.colors.textColorPrimary.enabled,
        )
        subtitle?.let {
            Text(
                text = subtitle,
                style = Theme.typography.albumSubtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Theme.colors.textColorSecondary.enabled,
            )
        }
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier = Modifier,
) {
    val quickAction = LocalThemeSettings.current.quickAction
    val iconRes = when (quickAction) {
        dev.olog.shared.android.theme.QuickAction.NONE -> return
        dev.olog.shared.android.theme.QuickAction.PLAY -> R.drawable.vd_play
        dev.olog.shared.android.theme.QuickAction.SHUFFLE -> R.drawable.vd_shuffle
    }
    // TODO hardcoded colors
    Box(
        modifier = modifier
            .size(dimensionResource(R.dimen.smallShuffleSize))
            .shadow(8.dp, CircleShape)
            .background(Color(0xDDf2f2f2), CircleShape)
            .clickable(
                // TODO increase click size?
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    // TODO onclick
                },
            )
            .padding(6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            modifier = Modifier.matchParentSize(),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0xFF797979))
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            val categories = MediaIdCategory.values().toList() -
                MediaIdCategory.HEADER -
                MediaIdCategory.PLAYING_QUEUE

            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                itemsIndexed(categories) { index, category ->
                    CanareeTheme(
                        themeSettings = ThemeSettingsOverride(
                            imageShape = ImageShape.values()[index % ImageShape.values().size],
                            quickAction = QuickAction.values()[index % QuickAction.values().size],
                        )
                    ) {
                        ListItemAlbum(
                            mediaId = MediaId.createCategoryValue(category, "$index"),
                            title = "Title",
                            subtitle = category.toString(),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {},
                            onLongClick = {},
                        )
                    }
                }
            }
        }
    }
}
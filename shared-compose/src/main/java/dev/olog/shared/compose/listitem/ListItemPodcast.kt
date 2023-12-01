package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.bumptech.glide.integration.compose.placeholder
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.CoverUtils
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.dynamicShape
import dev.olog.shared.compose.component.scaleDownOnTouch
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.ThemeSettingsOverride

@Composable
fun ListItemPodcast(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    duration: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    ListItemSlots(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .scaleDownOnTouch()
            .padding(paddingValues),
        leadingContent = {
            AsyncImage(
                model = mediaId,
                modifier = Modifier
                    .matchParentSize()
                    .dynamicShape(mediaId),
                placeholder = placeholder(CoverUtils.getGradient(LocalContext.current, mediaId)),
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
            Text(
                text =  duration,
                color = Theme.colors.accent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = Theme.spacing.small)
            )
        },
    )
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            val categories = MediaIdCategory.values().toList() -
                MediaIdCategory.HEADER -
                MediaIdCategory.PLAYING_QUEUE

            LazyColumn {
                itemsIndexed(categories) { index, category ->
                    CanareeTheme(
                        themeSettings = ThemeSettingsOverride(
                            imageShape = ImageShape.values()[index % ImageShape.values().size],
                        )
                    ) {
                        ListItemPodcast(
                            mediaId = MediaId.createCategoryValue(category, "$index"),
                            title = "title",
                            subtitle = "subtitle",
                            duration = "56m",
                            onClick = {},
                            onLongClick = {},
                        )
                    }
                }
            }
        }
    }
}
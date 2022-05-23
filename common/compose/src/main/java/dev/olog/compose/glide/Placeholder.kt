package dev.olog.compose.glide

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olog.compose.Background
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.ui.CoverUtils

@Composable
internal fun Placeholder(
    mediaId: MediaId,
    modifier: Modifier = Modifier,
) {
    val isDarkMode = isSystemInDarkTheme()
    val gradient = remember(mediaId) {
        val colors = CoverUtils.onlyGradientColors(
            mediaId = mediaId,
            isDarkMode = isDarkMode,
        )
        Brush.linearGradient(
            0f to Color(colors[0]),
            1f to Color(colors[1]),
            start = Offset.Zero,
            end = Offset.Infinite
        )
    }

    Box(
        modifier = modifier.background(gradient),
        contentAlignment = Alignment.Center,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(.35f)
                    .paint(
                        // todo double remember
                        painter = rememberVectorPainter(
                            ImageVector.vectorResource(CoverUtils.getDrawableId(mediaId))
                        ),
                        contentScale = ContentScale.Fit,
                        sizeToIntrinsics = true
                    )
            )
        },
    )
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Background(Modifier.fillMaxSize()) {
            LazyVerticalGrid(GridCells.Fixed(4)) {
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "1"),
                    )
                }
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.PLAYLISTS, "2"),
                    )
                }
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.SONGS, "3"),
                    )
                }
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.PODCASTS, "4"),
                    )
                }
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "5"),
                    )
                }
                item {
                    PreviewPlaceholder(
                        mediaId = MediaId.createCategoryValue(MediaIdCategory.ARTISTS, "6"),
                    )
                }
                item {
                    PreviewPlaceholder(MediaId.createCategoryValue(MediaIdCategory.GENRES, "7"))
                }
            }
        }
    }
}

@Composable
private fun PreviewPlaceholder(mediaId: MediaId) {
    Placeholder(
        mediaId = mediaId,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(2.dp),
    )
}
package dev.olog.shared.compose.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.DrawablePainter
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.CoverUtils
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun MediaIdPlaceholder(
    mediaId: MediaId,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val painter = remember(context, mediaId) {
        DrawablePainter(ContextCompat.getDrawable(context, CoverUtils.getDrawable(mediaId))!!)
    }
    // TODO improve
    val colors = remember(context, mediaId) {
        val colors = CoverUtils.getGradientColors(context, mediaId)
        colors.map { Color(it) }
    }
    Layout(
        modifier = modifier
            // TODO improve
            .background(Brush.linearGradient(colors)),
        content = {
            Image(
                painter = painter,
                contentDescription = null,
            )
        },
    ) { measurables, constraints ->
        val containerWidth = constraints.maxWidth
        val containerHeight = constraints.maxHeight
        val imageWidth: Int = (containerWidth / 2.5f).toInt()
        val imageHeight: Int = (containerHeight / 2.5f).toInt()

        val image = measurables[0]
        val placeable = image.measure(
            Constraints.fixed(imageWidth, imageHeight)
        )
        layout(containerWidth, containerHeight) {
            placeable.place(
                (containerWidth - imageWidth) / 2,
                (containerHeight - imageHeight) / 2
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier
                .background(Theme.colors.background)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MediaIdPlaceholder(MediaId.songId(2), Modifier.weight(1f).aspectRatio(1f))
                MediaIdPlaceholder(MediaId.songId(1), Modifier.weight(2f).aspectRatio(1f))
                MediaIdPlaceholder(MediaId.songId(3), Modifier.weight(3f).aspectRatio(1f))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val categories = MediaIdCategory.values().toList() -
                    MediaIdCategory.HEADER -
                    MediaIdCategory.PLAYING_QUEUE
                itemsIndexed(categories) { index, category ->
                    MediaIdPlaceholder(
                        MediaId.createCategoryValue(category, "$index"),
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}
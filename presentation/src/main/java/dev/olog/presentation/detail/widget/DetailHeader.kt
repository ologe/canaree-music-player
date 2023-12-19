package dev.olog.presentation.detail.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.shared.android.extensions.findFirstVisibleItemPosition
import dev.olog.shared.android.extensions.findParentByType
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

private val ShapeSize = 16.dp
private val Shape = RoundedCornerShape(topStart = ShapeSize, ShapeSize)
private val FadeGradient = listOf(
    Color(0x77000000),
    Color(0x11000000),
    Color.Transparent,
)

// TODO add colored ripple on image click?
@Composable
fun DetailHeader(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    biography: String?,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    val recyclerView = remember(view) {
        view.findParentByType<RecyclerView>()
    }
    val scroll = remember { mutableStateOf(0f) }
    DisposableEffect(recyclerView) {
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // TODO fix parallax
                if (recyclerView.findFirstVisibleItemPosition() == 0) {
                    scroll.value = scroll.value + dy * 0.7f // TODO tweak parallax
                }
            }
        }
        recyclerView?.addOnScrollListener(listener)
        onDispose {
            recyclerView?.removeOnScrollListener(listener)
        }
    }

    Layout(
        modifier = modifier,
        content = {
            AsyncImage(
                mediaId = mediaId,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clipToBounds()
                    .graphicsLayer {
                        translationY = scroll.value
                    }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = FadeGradient,
                                endY = 120.dp.toPx(), // TODO should not be needed, but it's bugged without
                            ),
                            topLeft = Offset.Zero,
                            size = Size(size.width, 120.dp.toPx()),
                        )
                    }
            )
            Column(
                modifier = Modifier
                    .background(Theme.colors.background, Shape)
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium),
            ) {
                Text(
                    text = title,
                    style = Theme.typography.headline,
                    color = Theme.colors.textColorPrimary.enabled,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    color = Theme.colors.primary.enabled,
                )
                if (biography != null) {
                    // TODO animate height change?
                    Spacer(Modifier.height(Theme.spacing.small))
                    Text(
                        text = biography, // TODO convert to html + convert to annotated string
                        maxLines = 3, // TODO allow to expand on click
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    ) { measurables, constraints ->
        val imagePlaceable = measurables[0].measure(constraints)
        val contentPlaceable = measurables[1].measure(constraints)

        val shapeSizePx = ShapeSize.roundToPx()
        val height = imagePlaceable.height +
            contentPlaceable.height -
            shapeSizePx

        layout(constraints.maxWidth, height) {
            imagePlaceable.place(0, 0)
            contentPlaceable.place(0, imagePlaceable.height - shapeSizePx)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailHeader(
                mediaId = MediaId.songId(1),
                title = "Title",
                subtitle = "Subtitle",
                biography = "Biography",
            )
        }
    }
}
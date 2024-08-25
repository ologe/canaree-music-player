package dev.olog.presentation.detail.ui

import android.view.View
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.findParentByType
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.AsyncImage
import dev.olog.shared.compose.glide.BindingAdapters
import kotlin.math.abs

private val FadeGradient = Brush.verticalGradient(
    listOf(Color(0x66_000000), Color(0x33_000000), Color.Transparent),
)
private val Corners = 16.dp
private val Shape = RoundedCornerShape(topStart = Corners, topEnd = Corners)
private const val PARALLAX_FACTOR = .7f

@Composable
fun DetailContent(
    mediaId: MediaId,
    title: String,
    subtitle: String,
    biography: String?,
    itemView: View,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScroll(itemView)

    HeaderLayout(modifier.fillMaxWidth()) {
        Box {
            AsyncImage(
                Modifier
                    .aspectRatio(1f)
                    .graphicsLayer {
                        val fraction = scroll.value / size.height
                        // TODO it overshoots on fling, fix
                        translationY = size.height * fraction * PARALLAX_FACTOR
                    }
            ) {
                BindingAdapters.loadBigAlbumImage(this, mediaId)
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .graphicsLayer { translationY = scroll.value }
                    .background(FadeGradient)
            )
        }
        Column(
            modifier = Modifier
                .background(Theme.colors.background, Shape)
                .padding(
                    horizontal = 12.dp,
                    vertical = 12.dp
                )
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Black,
                fontSize = 40.sp,
                color = Theme.colors.textColorPrimary,
            )
            Text(
                text = subtitle,
                fontSize = with(LocalDensity.current) { dimensionResource(id = R.dimen.item_detail_main_header_subtitle_size).toSp() },
                color = Theme.m3Colors.primary,
            )

            // TODO parse as html
            var isExpanded by remember { mutableStateOf(false) }
            biography?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = it,
                    style = LocalTextStyle.current,
                    fontSize = 13.sp,
                    color = Theme.colors.textColorPrimary,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    lineHeight = 1.4.em,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { isExpanded = !isExpanded }
                        )
                        .animateContentSize(),
                )
            }
        }
    }
}

@Composable
private fun rememberScroll(itemView: View): State<Float> {
    val state = remember { mutableStateOf(0f) }
    val view = LocalView.current
    val recyclerView = remember(view) { view.findParentByType<RecyclerView>() }

    DisposableEffect(recyclerView) {
        val listener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                state.value = abs(itemView.top.toFloat())
            }
        }
        recyclerView?.addOnScrollListener(listener)
        onDispose {
            recyclerView?.removeOnScrollListener(listener)
        }
    }

    return state
}

@Composable
private fun HeaderLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val imagePlaceable = measurables[0].measure(constraints)
        val contentPlaceable = measurables[1].measure(constraints)

        layout(
            constraints.maxWidth,
            imagePlaceable.height - Corners.roundToPx() + contentPlaceable.height
        ) {
            imagePlaceable.place(0, 0)
            contentPlaceable.place(0, imagePlaceable.height - Corners.roundToPx())
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailContent(
                MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                biography = null,
                itemView = LocalView.current,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Preview2() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            DetailContent(
                MediaId.songId(1),
                title = "Angel Beach",
                subtitle = "Trilogy",
                biography = "<b>Lorem ipsum</b> dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
                        "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                        "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure " +
                        "dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                        "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit " +
                        "anim id est laborum.",
                itemView = LocalView.current,
            )
        }
    }
}
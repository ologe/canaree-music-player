package dev.olog.shared.compose.screen

import android.app.Activity
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dev.olog.shared.android.extensions.findInContext
import dev.olog.shared.compose.R
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.component.Toolbar
import dev.olog.shared.compose.extension.plus
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import kotlin.math.abs

@Composable
fun Screen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colors.background,
    toolbarColor: Color = Theme.colors.surface,
    fullscreenContent: Boolean = false,
    statusBarContent: @Composable () -> Unit = {
        StatusBar(color = if (fullscreenContent) Color.Unspecified else toolbarColor)
    },
    fabContent: @Composable (PaddingValues) -> Unit = {},
    toolbarContent: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollManager = rememberScrollManager()
    val scrollState = remember { mutableFloatStateOf(0f) }
    val maxScrollState = remember { mutableFloatStateOf(0f) }
    val slidingPanel = rememberSlidingPanel()
    val bottomNavigation = rememberBottomNavigation()

    DisposableEffect(scrollManager) {
        val callback: (Float) -> Unit = { dy ->
            scrollState.floatValue = (scrollState.floatValue + dy)
                .coerceIn(-maxScrollState.floatValue, 0f)
        }
        scrollManager.addScrollListener(callback)
        onDispose {
            scrollManager.removeScrollListener(callback)
        }
    }

    SubcomposeLayout(
        modifier = modifier.background(backgroundColor)
    ) { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val statusBarPlaceables = subcompose(ScreenSlots.StatusBar, statusBarContent)
            .map { it.measure(looseConstraints) }
        val statusBarHeight = statusBarPlaceables.maxOf { it.height }

        val toolbarPlaceablesPass1 = subcompose(ScreenSlots.ToolbarPass1, toolbarContent)
            .map { it.measure(looseConstraints) }
        val scrollableHeight = toolbarPlaceablesPass1.getOrNull(0)?.height ?: 0
        val nonScrollableHeight = toolbarPlaceablesPass1.getOrNull(1)?.height ?: 0
        maxScrollState.floatValue = scrollableHeight.toFloat()

        val toolbarPlaceablesPass2 = subcompose(ScreenSlots.ToolbarPass2) {
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = scrollState.floatValue
                        if (fullscreenContent) {
                            // TODO check on different screens
                            alpha = 1f - abs(scrollState.floatValue) * 0.005f
                        }
                    }
                    .then(if (fullscreenContent) Modifier else Modifier.shadow(2.dp))
                    .background(toolbarColor)
            ) {
                toolbarContent()
            }
        }.map { it.measure(looseConstraints) }

        val contentPlaceables = subcompose(ScreenSlots.Content) {
            Box(Modifier.dispatchListScroll()) {
                val topPadding = if (fullscreenContent) statusBarHeight else scrollableHeight
                content(PaddingValues(top = topPadding.toDp()))
            }
        }.map { it.measure(looseConstraints.copy(maxHeight = layoutHeight - statusBarHeight - nonScrollableHeight)) }

        val fabPlaceables = subcompose(ScreenSlots.Fab) {
            Box(
                Modifier.graphicsLayer {
                    // TODO scroll as much as bottom navigation and not toolbar
                    translationY = -scrollState.floatValue
                },
            ) {
                val density = LocalDensity.current
                val bottomPadding = (slidingPanel?.peekHeight ?: 0) - (bottomNavigation?.height ?: 0)
                fabContent(
                    PaddingValues(Theme.spacing.medium) +
                        PaddingValues(bottom = with(density) { bottomPadding.toDp() })
                )
            }
        }.map { it.measure(looseConstraints) }

        layout(layoutWidth, layoutHeight) {
            for (placeable in contentPlaceables) {
                placeable.place(0, if (fullscreenContent) 0 else statusBarHeight + nonScrollableHeight)
            }

            for (placeable in toolbarPlaceablesPass2) {
                placeable.place(0, statusBarHeight)
            }

            for (placeable in statusBarPlaceables) {
                placeable.place(0, 0)
            }

            for (placeable in fabPlaceables) {
                placeable.place(
                    layoutWidth - placeable.width,
                    layoutHeight - placeable.height,
                )
            }
        }
    }

}

@Composable
private fun rememberSlidingPanel(): BottomSheetBehavior<*>? {
    if (LocalInspectionMode.current) {
        return null
    }
    val context = LocalContext.current
    return remember(context) {
        BottomSheetBehavior.from(context.findInContext<Activity>().window.decorView.findViewById(R.id.slidingPanel))
    }
}

@Composable
private fun rememberBottomNavigation(): View? {
    if (LocalInspectionMode.current) {
        return null
    }
    val context = LocalContext.current
    return remember(context) {
        context.findInContext<Activity>().window.decorView.findViewById(R.id.bottomWrapper)
    }
}

private enum class ScreenSlots {
    StatusBar,
    ToolbarPass1,
    ToolbarPass2,
    Fab,
    Content,
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Screen(
            toolbarContent = {
                Toolbar(
                    title = {
                        Text(text = "Toolbar")
                    }
                )
            },
            fabContent = {
                Spacer(
                    Modifier
                        .padding(it)
                        .size(48.dp)
                        .background(Theme.colors.primary.enabled, CircleShape)

                )
            }
        ) {
            Spacer(
                Modifier
                    .padding(it)
                    .fillMaxSize()
                    .background(Theme.colors.textColorPrimary.enabled)

            )
        }
    }
}
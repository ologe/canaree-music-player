package dev.olog.shared.compose.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olog.core.MediaIdCategory
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.extension.visiblePage
import dev.olog.shared.compose.extension.visiblePageOffset
import dev.olog.shared.compose.screen.Screen
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalContentColor
import dev.olog.shared.compose.theme.LocalTextStyle
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.lerp

private val VerticalPadding = 8.dp
private val HorizontalItemPadding = 12.dp
private val VerticalItemPadding = 6.dp
private val TabShape = RoundedCornerShape(24.dp)
private val EdgePadding = 16.dp
private val IndicatorHorizontalInset = 2.dp

@Composable
fun TabRow(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    indicator: @Composable () -> Unit = { Indicator() },
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val tabsData = remember { TabsData(density, scrollState, emptyList()) }

    LaunchedEffect(pagerState, scrollState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                val amount = tabsData.centeredTabScrollOffset(page)
                scrollState.animateScrollTo(amount)
            }
    }

    SubcomposeLayout(
        modifier = modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth(),
    ) { constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val tabsPlaceables = subcompose(TabsSlot.Tabs, content)
            .map { it.measure(looseConstraints) }

        tabsData.items = tabsPlaceables.map { IntSize(it.width, it.height) }
        val visiblePage = pagerState.visiblePage
        val visiblePageOffset = pagerState.visiblePageOffset

        val indicatorWidth = lerp(
            tabsData.widthFor(visiblePage).toFloat(),
            tabsData.widthFor(visiblePage + 1).toFloat(),
            visiblePageOffset
        ).coerceAtLeast(0f)

        val indicatorPlaceables = subcompose(TabsSlot.Indicator, indicator)
            .map { it.measure(Constraints.fixedWidth(indicatorWidth.toInt())) }

        val layoutWidth = tabsData.width
        val layoutHeight = tabsData.height
        layout(layoutWidth, layoutHeight) {
            var x = EdgePadding.roundToPx()
            for (placeable in tabsPlaceables) {
                placeable.place(x, 0)
                x += placeable.width
            }

            for (placeable in indicatorPlaceables) {
                placeable.place(
                    x = lerp(
                        tabsData.positionFor(visiblePage).toFloat(),
                        tabsData.positionFor(visiblePage + 1).toFloat(),
                        visiblePageOffset,
                    ).toInt(),
                    y = layoutHeight - placeable.height
                )
            }
        }
    }
}

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource,
            )
            .padding(vertical = VerticalPadding)
            .clip(TabShape)
            .indication(
                interactionSource,
                LocalIndication.current,
            )
            .padding(vertical = VerticalItemPadding, horizontal = HorizontalItemPadding)
    ) {
        CompositionLocalProvider(
            // TODO style
            LocalTextStyle provides Theme.typography.body.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                letterSpacing = 0.1.sp
            ),
            LocalContentColor provides if (selected) Theme.colors.primary else LocalContentColor.current
        ) {
            content()
        }
    }
}

@Composable
private fun Indicator() {
    val cornerSize = 3.dp
    val corners = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize)
    Spacer(
        Modifier
            // fix weird gap
            .graphicsLayer { translationY = 2f }
            .height(2.5.dp)
            .fillMaxWidth()
            .background(Theme.colors.primary.enabled, corners)
    )
}

private class TabsData(
    val density: Density,
    val scrollState: ScrollState,
    var items: List<IntSize>,
) {

    val width: Int
        get() = items.sumOf { it.width } + with(density) { EdgePadding.roundToPx() * 2 }

    val height: Int
        get() = items.maxOfOrNull { it.height } ?: 0

    fun widthFor(index: Int): Int = with(density) {
        return (items.getOrNull(index)?.width ?: 0) -
            HorizontalItemPadding.roundToPx() * 2 -
            IndicatorHorizontalInset.roundToPx() * 2
    }

    fun positionFor(index: Int): Int = with(density) {
        var width = EdgePadding.roundToPx() +
            HorizontalItemPadding.roundToPx() +
            IndicatorHorizontalInset.roundToPx()

        for (i in 0 until index) {
            width += (items.getOrNull(i)?.width ?: 0)
        }

        return width
    }

    fun centeredTabScrollOffset(page: Int): Int = with(density) {
        val totalTabRowWidth = width
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val tabOffset = positionFor(page)
        val scrollerCenter = visibleWidth / 2
        val tabWidth = widthFor(page)
        val centeredTabOffset = tabOffset - (scrollerCenter - tabWidth / 2)
        // How much space we have to scroll. If the visible width is <= to the total width, then
        // we have no space to scroll as everything is always visible.
        val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        centeredTabOffset.coerceIn(0, availableSpace)
    }

}

private enum class TabsSlot {
    Tabs,
    Indicator,
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        val items = MediaIdCategory.entries
        val pagerState = rememberPagerState { items.size }
        Screen(
            toolbarContent = {
                TabRow(pagerState) {
                    for ((index, item) in items.withIndex()) {
                        Tab(
                            selected = index == pagerState.currentPage,
                            onClick = { /*TODO*/ },
                            content = { Text(text = item.name) }
                        )
                    }
                }
            }
        ) {
            HorizontalPager(pagerState) {
                Spacer(Modifier.fillMaxSize())
            }
        }
    }
}
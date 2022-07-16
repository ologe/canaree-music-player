package dev.olog.compose.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState

@Composable
fun CanareePagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageIndexMapping: (Int) -> Int = { it },
    activeColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    inactiveColor: Color = activeColor.copy(ContentAlpha.disabled),
) {
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = modifier,
        pageCount = pagerState.pageCount,
        pageIndexMapping = pageIndexMapping,
        activeColor = activeColor,
        inactiveColor = inactiveColor,
        indicatorWidth = 6.dp,
        indicatorHeight = 6.dp,
        spacing = 6.dp,
        indicatorShape = RoundedCornerShape(2.dp),
    )
}
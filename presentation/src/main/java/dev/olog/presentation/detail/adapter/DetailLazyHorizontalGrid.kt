package dev.olog.presentation.detail.adapter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.olog.shared.compose.listitem.ListItemSlotsHeight

@Composable
fun <T> DetailLazyHorizontalGrid(
    items: List<T>,
    maxRows: Int = 4,
    content: @Composable LazyGridItemScope.(T) -> Unit,
) {
    val rowsSize = items.size.coerceAtMost(maxRows)

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val maxWidth = with(LocalDensity.current) { constraints.maxWidth.toDp() }
        LazyHorizontalGrid(
            rows = GridCells.Fixed(rowsSize),
            // TODO try to remove hardcoded height after compose bump
            modifier = Modifier.height(rowsSize * ListItemSlotsHeight),
        ) {
            items(items) { item ->
                Box(modifier = Modifier.width(maxWidth - 24.dp)) {
                    content(item)
                }
            }
        }
    }
}
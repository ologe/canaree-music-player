package dev.olog.shared.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.olog.shared.groupByRows

@Composable
fun<T> GridList(
    list: List<T>,
    spanCount: Int,
    rowHeaderContent: @Composable (ColumnScope.(Int, List<T>) -> Unit)? = null,
    itemContent: @Composable RowScope.(T) -> Unit
) {
    val gridList by remember(list, spanCount) {
        mutableStateOf(list.groupByRows(spanCount))
    }
    LazyColumnForIndexed(items = gridList) { i, items ->
        Column {
            rowHeaderContent?.invoke(this, i, items)
            Row {
                for (index in 0 until spanCount) {
                    val current = items.getOrNull(index)
                    Box(modifier = Modifier.weight(1f)) {
                        if (current != null) {
                            itemContent(current)
                        }
                    }
                }
            }
        }
    }
}
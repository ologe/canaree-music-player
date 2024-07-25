package dev.olog.shared.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.DpSize

// LazyHorizontalGrid breaks when inside another scrollable item and needs a fixed
// height, so calculate first [itemContent] size and pass it do [content]
@Composable
fun LazyHorizontalGridFix(
    itemContent: @Composable () -> Unit,
    content: @Composable (DpSize) -> Unit
) {
    SubcomposeLayout {
        val itemPlaceable = subcompose(0) { itemContent() }
            .first().measure(it)
        val itemSize = DpSize(itemPlaceable.width.toDp(), itemPlaceable.height.toDp())

        val listPlaceable = subcompose(1) { content(itemSize) }
            .first().measure(it)
        layout(listPlaceable.width, listPlaceable.height) {
            listPlaceable.place(0, 0)
        }
    }
}
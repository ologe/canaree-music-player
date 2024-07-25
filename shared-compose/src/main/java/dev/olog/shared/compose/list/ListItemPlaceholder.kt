package dev.olog.shared.compose.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olog.shared.compose.list.internal.ListItemRow

@Composable
fun ListItemPlaceholder(modifier: Modifier = Modifier) {
    ListItemRow(
        title = {},
        subtitle = {},
        leadingContent = {},
        trailingContent = {},
        modifier = modifier,
        onClick = {},
        onLongClick = {}
    )
}
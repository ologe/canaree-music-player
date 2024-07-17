package dev.olog.shared.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

@Composable
fun FixedSizeLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier,
    ) { measurables, constraints ->
        val fixedConstraints = Constraints.fixed(
            constraints.maxWidth,
            constraints.maxHeight,
        )
        val placeables = measurables.map { it.measure(fixedConstraints) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            for (placeable in placeables) {
                placeable.place(0, 0)
            }
        }

    }

}
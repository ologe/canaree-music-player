package dev.olog.compose.modifier

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

fun Modifier.elevation(
    elevation: Dp,
    shape: Shape = RectangleShape,
    color: Color,
): Modifier {
    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false
        )
        .background(
            color = color,
            shape = shape
        )
}
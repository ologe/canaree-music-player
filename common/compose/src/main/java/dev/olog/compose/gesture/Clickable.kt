package dev.olog.compose.gesture

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

fun Modifier.clickable(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    enabled: Boolean = true,
    withRipple: Boolean = true,
): Modifier = composed {
    if (withRipple) {
        this.combinedClickable(
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    } else {
        this.combinedClickable(
            enabled = enabled,
            onClick = onClick,
            onLongClick = onLongClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() },
        )
    }
}
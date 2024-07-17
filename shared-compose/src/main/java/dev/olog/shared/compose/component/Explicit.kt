package dev.olog.shared.compose.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explicit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp

@Composable
fun Explicit(title: String) {
    // TODO is this costly?
    if (LocalInspectionMode.current || title.contains("explicit", ignoreCase = true)) {
        Icon(
            imageVector = Icons.Rounded.Explicit,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
        )
    }
}
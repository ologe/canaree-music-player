package dev.olog.shared.components

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olog.shared.components.ambient.statusBarsHeight

@Composable
fun StatusBar() {
    Surface(
        elevation = ToolbarElevation,
        color = MaterialTheme.colors.primaryVariant,
    ) {
        Box(modifier = Modifier.fillMaxWidth().statusBarsHeight())
    }
}
package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Capsule(modifier: Modifier) {
    Spacer(
        modifier.background(Color.LightGray, RoundedCornerShape(16.dp))
    )
}
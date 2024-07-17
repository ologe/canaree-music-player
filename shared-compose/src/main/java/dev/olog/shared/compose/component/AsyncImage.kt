package dev.olog.shared.compose.component

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    update: ImageView.() -> Unit,
) {
    if (LocalInspectionMode.current) {
        Spacer(modifier.background(Color.Cyan))
    } else {
        AndroidView(
            factory = { ImageView(it) },
            modifier = modifier,
            update = { update(it) }
        )
    }
}
package dev.olog.shared.compose.component

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier,
    update: ImageView.() -> Unit,
) {
    if (LocalInspectionMode.current) {
        Image(
            painter = painterResource(R.drawable.preview),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } else {
        AndroidView(
            factory = { ImageView(it) },
            modifier = modifier,
            update = { update(it) }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        AsyncImage(Modifier.fillMaxWidth().aspectRatio(1f)) {}
    }
}
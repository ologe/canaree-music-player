package dev.olog.shared.components.image

import androidx.compose.foundation.Image
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ContextAmbient
import androidx.core.graphics.drawable.toBitmap
import dev.olog.domain.MediaId
import dev.olog.lib.image.loader.CoverUtils
import dev.olog.shared.components.ambient.CustomTheming
import dev.olog.shared.components.ambient.ImageShapeAmbient
import dev.olog.shared.components.ambient.shape

@Composable
fun GlideView(
    mediaId: MediaId,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val image = fetchSongImage(mediaId = mediaId)

    val context = ContextAmbient.current
    val placeholder by remember {
        // TODO is this slow?? conversion to bitmap then to image asset
        mutableStateOf(CoverUtils.getGradient(context, mediaId).toBitmap().asImageAsset())
    }

    // TODO is correct transparent color?
    //   but without surface placeholder is very big
    Surface(modifier, color = Color.Transparent) {
        // TODO crossfade
        Image(
            asset = image ?: placeholder,
            modifier = Modifier.clip(CustomTheming.imageShape),
            contentScale = contentScale
        )
    }
}
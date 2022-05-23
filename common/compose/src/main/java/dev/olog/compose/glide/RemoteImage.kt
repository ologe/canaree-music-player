package dev.olog.compose.glide

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.bumptech.glide.RequestBuilder
import com.skydoves.landscapist.glide.GlideImageState
import com.skydoves.landscapist.glide.toGlideImageState
import com.skydoves.landscapist.rememberDrawablePainter
import dev.olog.core.MediaId
import dev.olog.shared.extension.exhaustive

@Composable
fun RemoteImage(
    mediaId: MediaId,
    modifier: Modifier = Modifier,
    requestBuilder: @Composable () -> RequestBuilder<Drawable> = {
        LocalGlideProvider.getGlideRequestBuilder(mediaId)
    },
) {
    if (LocalInspectionMode.current) {
        Placeholder(
            mediaId = mediaId,
            modifier = modifier,
        )
        return
    }

    GlideImage(
        recomposeKey = mediaId,
        builder = requestBuilder.invoke().load(mediaId),
    ) ImageRequest@{ imageState ->
        when (val state = imageState.toGlideImageState()) {
            is GlideImageState.None,
            is GlideImageState.Loading -> {
                Placeholder(
                    mediaId = mediaId,
                    modifier = modifier,
                )
            }
            is GlideImageState.Failure -> {
                Placeholder(
                    mediaId = mediaId,
                    modifier = modifier,
                )
            }
            is GlideImageState.Success -> {
                val drawable = state.drawable ?: return@ImageRequest
                Image(
                    painter = rememberDrawablePainter(drawable),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
        }.exhaustive
    }
}
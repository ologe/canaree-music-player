package dev.olog.shared.components.image

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageAsset
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.ContextAmbient
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dev.olog.domain.MediaId
import dev.olog.lib.image.loader.GlideUtils

@Composable
fun fetchSongImage(
    mediaId: MediaId
): ImageAsset? {
    return fetchGlideBitmap(mediaId, GlideUtils.OVERRIDE_SMALL)
}

@Composable
private fun fetchGlideBitmap(
    mediaId: MediaId,
    override: Int,
    priority: Priority = Priority.HIGH
): ImageAsset? {
    var image by remember { mutableStateOf<ImageAsset?>(null) }
    val context = ContextAmbient.current

    onCommit(mediaId) {
        val listener = object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean
            ): Boolean {
                e?.printStackTrace()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                _model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                image = resource?.asImageAsset()
                return true
            }
        }
        val target = try {
             Glide.with(context)
                .asBitmap()
                .load(mediaId)
                .override(override)
                .priority(priority)
                .addListener(listener)
//                .placeholder(CoverUtils.getGradient(context, mediaId)) TODO placeholder don't work
//                .transition(BitmapTransitionOptions.withCrossFade()) // TODO crossfade don't work
                .preload(override, override)
        } catch (ex: Throwable) {
            // TODO compose cannot initialize glide (yet?)
            null
        }

        onDispose {
            // TODO crash, cannot load image on destroyed activity??
            Glide.with(context).clear(target)
        }
    }
    return image
}
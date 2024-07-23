package dev.olog.presentation.widgets.switcher

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

suspend fun Context.loadPlayerCachedImage(
    mediaId: MediaId
): Flow<Drawable> = channelFlow {
    val context = this@loadPlayerCachedImage
    val placeholder = CoverUtils.onlyGradient(context, mediaId)
    val error = CoverUtils.getGradient(context, mediaId)
    send(placeholder)

    GlideApp.with(context)
        .load(mediaId)
        .priority(Priority.IMMEDIATE)
        .override(GlideUtils.OVERRIDE_BIG)
        .onlyRetrieveFromCache(true)
        .into(
            CallbackTarget(
                onSuccess = {
                    trySend(it)
                    close()
                },
                onFailure = {
                    trySend(error)
                    close()
                }
            )
        )
    awaitCancellation()
}

suspend fun Context.loadPlayerImage(
    mediaId: MediaId,
    withPlaceholder: Boolean,
): Flow<Drawable> = channelFlow {
    val context = this@loadPlayerImage
    if (withPlaceholder) {
        val placeholder = CoverUtils.onlyGradient(context, mediaId)
        send(placeholder)
    }

    GlideApp.with(context)
        .load(mediaId)
        .priority(Priority.IMMEDIATE)
        .override(GlideUtils.OVERRIDE_BIG)
        .into(
            CallbackTarget(
                onSuccess = {
                    trySend(it)
                    close()
                },
                onFailure = {
                    if (withPlaceholder) {
                        val error = CoverUtils.getGradient(context, mediaId)
                        trySend(error)
                    }
                    close()
                }
            )
        )
    awaitCancellation()
}

private class CallbackTarget(
    private val onSuccess: (Drawable) -> Unit,
    private val onFailure: (Drawable?) -> Unit,
) : CustomTarget<Drawable>() {

    private var terminated = false

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        tryEmit { onSuccess(resource) }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        tryEmit { onFailure(errorDrawable) }
    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }

    private fun tryEmit(callback: () -> Unit) {
        if (!terminated) {
            terminated = true
            callback()
        }
    }

}
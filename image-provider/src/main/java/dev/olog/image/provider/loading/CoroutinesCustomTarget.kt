package dev.olog.image.provider.loading

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

internal class CoroutinesCustomTarget<T : Any>(
    width: Int,
    height: Int,
    private val continuation: CancellableContinuation<T?>,
) : CustomTarget<T>(width, height) {

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
        continuation.resume(resource)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        continuation.resume(null)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        continuation.cancel()
    }
}
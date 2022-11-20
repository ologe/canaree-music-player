package dev.olog.image.provider.loading

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

// actual data is handled by CoroutinesDrawableRequestListener
internal class CoroutinesDrawableCustomTarget(
    width: Int,
    height: Int,
) : CustomTarget<Drawable>(width, height) {

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) = Unit
    override fun onLoadFailed(errorDrawable: Drawable?) = Unit
    override fun onLoadCleared(placeholder: Drawable?) = Unit
}
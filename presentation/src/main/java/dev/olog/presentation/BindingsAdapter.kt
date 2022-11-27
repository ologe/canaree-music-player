package dev.olog.presentation

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.DrawableImageViewTarget
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.loading.ImageSize
import dev.olog.presentation.ripple.RippleTarget

object BindingsAdapter {

    @JvmStatic
    private fun loadImageImpl(
        view: ImageView,
        mediaId: MediaId,
        imageSize: ImageSize,
        priority: Priority = Priority.HIGH
    ) {
        val context = view.context

        val builder = GlideApp.with(context)
            .load(mediaId)
            .override(imageSize.size)
            .priority(priority)
            .placeholder(CoverUtils.full(context, mediaId))

        if (mediaId.isLeaf) {
            builder.into(DrawableImageViewTarget(view))
        } else {
            builder.into(RippleTarget(view))
        }
    }

    @JvmStatic
    fun loadSongImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view,
            mediaId,
            ImageSize.Small
        )
    }

    @JvmStatic
    fun loadAlbumImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view,
            mediaId,
            ImageSize.Medium,
            Priority.HIGH
        )
    }

    @JvmStatic
    fun loadBigAlbumImage(view: ImageView, mediaId: MediaId) {
        val context = view.context

        GlideApp.with(context)
            .load(mediaId)
            .override(ImageSize.Large.size)
            .priority(Priority.IMMEDIATE)
            .placeholder(CoverUtils.onlyGradient(context, mediaId))
            .error(CoverUtils.full(context, mediaId))
            .onlyRetrieveFromCache(true)
            .into(RippleTarget(view))
    }

    @JvmStatic
    fun setBoldIfTrue(view: TextView, setBold: Boolean) {
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

}
package dev.olog.presentation

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import dev.olog.presentation.ripple.RippleTarget

object BindingsAdapter {

    private fun loadImageImpl(
        view: ImageView,
        mediaId: MediaId,
        override: Int,
        priority: Priority = Priority.HIGH
    ) {
        val context = view.context

        GlideApp.with(context).clear(view)

        val builder = GlideApp.with(context)
            .load(mediaId)
            .override(override)
            .priority(priority)
            .placeholder(CoverUtils.getGradient(context, mediaId))
            .transition(DrawableTransitionOptions.withCrossFade())

        if (mediaId.isLeaf) {
            builder.into(view)
        } else {
            builder.into(RippleTarget(view))
        }
    }

    fun loadSongImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_SMALL
        )
    }

    fun loadAlbumImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_MID,
            Priority.HIGH
        )
    }

    fun loadBigAlbumImage(view: ImageView, mediaId: MediaId) {
        val context = view.context

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
            .load(mediaId)
            .override(GlideUtils.OVERRIDE_BIG)
            .priority(Priority.IMMEDIATE)
            .placeholder(CoverUtils.onlyGradient(context, mediaId))
            .error(CoverUtils.getGradient(context, mediaId))
            .onlyRetrieveFromCache(true)
            .into(RippleTarget(view))
    }

    fun setBoldIfTrue(view: TextView, setBold: Boolean) {
        val style = if (setBold) Typeface.BOLD else Typeface.NORMAL
        view.setTypeface(null, style)
    }

}
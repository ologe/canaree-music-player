package dev.olog.shared.compose.glide

import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils

object BindingAdapters {

    fun loadSongImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view = view,
            mediaId = mediaId,
            override = GlideUtils.OVERRIDE_SMALL
        )
    }

    fun loadAlbumImage(view: ImageView, mediaId: MediaId) {
        loadImageImpl(
            view = view,
            mediaId = mediaId,
            override = GlideUtils.OVERRIDE_MID,
            priority = Priority.HIGH
        )
    }

    fun loadImageImpl(
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

        builder.into(view)
    }

}
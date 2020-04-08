package dev.olog.feature.presentation.base

import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.domain.MediaId
import dev.olog.feature.presentation.base.ripple.RippleTarget
import dev.olog.lib.image.loader.CoverUtils
import dev.olog.lib.image.loader.GlideApp
import dev.olog.lib.image.loader.GlideUtils
import dev.olog.shared.exhaustive

// TODo rename file
// TODO should it depend on glide??
fun ImageView.loadSongImage(mediaId: MediaId) {
    loadImageImpl(
        mediaId,
        GlideUtils.OVERRIDE_SMALL
    )
}

fun ImageView.loadAlbumImage(mediaId: MediaId) {
    loadImageImpl(
        mediaId,
        GlideUtils.OVERRIDE_MID,
        Priority.HIGH
    )
}

fun ImageView.loadBigAlbumImage(mediaId: MediaId) {
    GlideApp.with(context).clear(this)

    val thumbnail = GlideApp.with(context)
        .load(mediaId)
        .override(GlideUtils.OVERRIDE_SMALL)
        .priority(Priority.IMMEDIATE)
        .placeholder(CoverUtils.onlyGradient(context, mediaId))
        .error(CoverUtils.getGradient(context, mediaId))
        .onlyRetrieveFromCache(true)

    GlideApp.with(context)
        .load(mediaId)
        .thumbnail(thumbnail)
        .override(GlideUtils.OVERRIDE_BIG)
        .priority(Priority.HIGH)
        .placeholder(CoverUtils.onlyGradient(context, mediaId))
        .error(CoverUtils.getGradient(context, mediaId))
        .onlyRetrieveFromCache(true)
        .into(RippleTarget(this))
}

private fun ImageView.loadImageImpl(
    mediaId: MediaId,
    override: Int,
    priority: Priority = Priority.HIGH
) {
    GlideApp.with(context).clear(this)

    val builder = GlideApp.with(context)
        .load(mediaId)
        .override(override)
        .priority(priority)
        .placeholder(CoverUtils.getGradient(context, mediaId))
        .transition(DrawableTransitionOptions.withCrossFade())

    when (mediaId) {
        is MediaId.Track -> builder.into(this)
        is MediaId.Category -> builder.into(RippleTarget(this))
    }.exhaustive
}
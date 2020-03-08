package dev.olog.presentation

import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import dev.olog.image.provider.model.AudioFileCover
import dev.olog.presentation.model.DisplayableFile
import dev.olog.presentation.ripple.RippleTarget

fun ImageView.loadFile(item: DisplayableFile) {
    GlideApp.with(context).clear(this)

    GlideApp.with(context)
        .load(AudioFileCover(item.path!!))
        .override(GlideUtils.OVERRIDE_SMALL)
        .placeholder(CoverUtils.getGradient(context, MediaId.songId(item.path.hashCode().toLong())))
        .into(this)
}

fun ImageView.loadDirImage(item: DisplayableFile) {
    val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, item.path ?: "")
    loadImageImpl(
        mediaId,
        GlideUtils.OVERRIDE_SMALL
    )
}

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

    if (mediaId.isLeaf) {
        builder.into(this)
    } else {
        builder.into(RippleTarget(this))
    }
}
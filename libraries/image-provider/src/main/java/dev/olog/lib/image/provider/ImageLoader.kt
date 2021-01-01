package dev.olog.lib.image.provider

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.lib.image.provider.model.AudioFileCover
import dev.olog.lib.image.provider.target.RippleTarget
import dev.olog.shared.exhaustive

object ImageLoader {

    fun loadFile(view: ImageView, path: String?) {
        val context = view.context
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(AudioFileCover(path ?: ""))
                .override(GlideUtils.OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context, MediaId.songId(path.hashCode().toLong())))
                .into(view)
    }

    fun loadDirImage(view: ImageView, path: String?) {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, path ?: "invalid path")
        loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_SMALL
        )
    }

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

        when (mediaId) {
            is MediaId.Category -> builder.into(RippleTarget(view))
            is MediaId.Track -> builder.into(view)
        }.exhaustive
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
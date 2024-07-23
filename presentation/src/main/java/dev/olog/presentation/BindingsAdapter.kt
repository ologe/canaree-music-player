package dev.olog.presentation

import android.widget.ImageView
import com.bumptech.glide.Priority
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import dev.olog.image.provider.model.AudioFileCover
import dev.olog.presentation.model.DisplayableFile
import dev.olog.shared.compose.glide.BindingAdapters

@Deprecated("replace with dev.olog.shared.compose.glide.BindingsAdapter")
object BindingsAdapter {

    @JvmStatic
    fun loadFile(view: ImageView, item: DisplayableFile) {
        val context = view.context
        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(AudioFileCover(item.path!!))
                .override(GlideUtils.OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context, MediaId.songId(item.path.hashCode().toLong())))
                .into(view)
    }

    @JvmStatic
    fun loadDirImage(view: ImageView, item: DisplayableFile) {
        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, item.path ?: "")
        BindingAdapters.loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_SMALL
        )
    }

    @JvmStatic
    fun loadSongImage(view: ImageView, mediaId: MediaId) {
        BindingAdapters.loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_SMALL
        )
    }

    @JvmStatic
    fun loadAlbumImage(view: ImageView, mediaId: MediaId) {
        BindingAdapters.loadImageImpl(
            view,
            mediaId,
            GlideUtils.OVERRIDE_MID,
        )
    }

    @JvmStatic
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
            .into(view)
    }

}
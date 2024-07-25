package dev.olog.presentation

import android.widget.ImageView
import dev.olog.core.MediaId
import dev.olog.image.provider.GlideUtils
import dev.olog.shared.compose.glide.BindingAdapters

@Deprecated("replace with dev.olog.shared.compose.glide.BindingsAdapter")
object BindingsAdapter {

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

}
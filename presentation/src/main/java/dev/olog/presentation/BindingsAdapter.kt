package dev.olog.presentation

import android.databinding.BindingAdapter
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dev.olog.presentation.images.CoverUtils

object BindingsAdapter {

    private val OVERRIDE_SMALL = 150
    private val OVERRIDE_MID = 300
    private val OVERRIDE_BIG = 600


    @BindingAdapter("imageSong", "position")
    @JvmStatic
    fun loadSongImage(view: ImageView, image: String?, position: Int) {
        if (image == null) {
            return
        }

        val context = view.context

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_SMALL)
                .placeholder(CoverUtils.getGradient(context = context, position = position))
                .into(view)
    }

    @BindingAdapter("imageAlbum", "source", "position")
    @JvmStatic
    fun loadAlbumImage(view: ImageView, image: String, source: Int, position: Int) {
        val context = view.context

        GlideApp.with(context).clear(view)

        GlideApp.with(context)
                .load(Uri.parse(image))
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(OVERRIDE_MID)
                .error(CoverUtils.getGradient(context = context, position = position, source = source))
                .into(view)
    }

}
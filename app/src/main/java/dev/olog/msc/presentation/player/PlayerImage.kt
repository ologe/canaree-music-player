package dev.olog.msc.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.k.extension.getImage
import dev.olog.msc.utils.k.extension.getMediaId
import kotlinx.android.synthetic.main.layout_swipeable_view.view.*

object PlayerImage {

    fun loadImage(view: ImageView, metadata: MediaMetadataCompat){
        val mediaId = metadata.getMediaId()

        val context = view.context

        GlideApp.with(context).clear(view.cover)

        GlideApp.with(context)
                .load(metadata.toDisplayableItem())
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(800)
                .into(view)
    }

    private fun MediaMetadataCompat.toDisplayableItem(): DisplayableItem {
        // only mediaId and image is needed
        return DisplayableItem(0, this.getMediaId(), "", image = this.getImage())
    }

}
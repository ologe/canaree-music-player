package dev.olog.msc.presentation.widget.image.view

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.olog.msc.app.GlideApp
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.isMarshmallow
import dev.olog.msc.utils.k.extension.getImage
import dev.olog.msc.utils.k.extension.getMediaId
import dev.olog.msc.utils.k.extension.isPaused
import dev.olog.msc.utils.k.extension.isPlaying

class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : RoundedCornersImageView(context, attr) {

    fun loadImage(metadata: MediaMetadataCompat){
        val mediaId = metadata.getMediaId()

        val model = metadata.toPlayerImage()

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(model)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(800)
                .into(this)
    }

    fun toggleElevation(state: PlaybackStateCompat){
        if (state.isPlaying() || state.isPaused()){
            isActivated = state.isPlaying()
        }
    }

    fun forceRipple(x: Float, y: Float){
        if (isMarshmallow()){
            background.setHotspot(x, y)
            isPressed = true
            isPressed = false
        } else {
            // ripple looks bad on lollipop
        }
    }

}

fun MediaMetadataCompat.toPlayerImage(): DisplayableItem {
    // only mediaId and image is needed
    return DisplayableItem(0, this.getMediaId(), "", image = this.getImage())
}


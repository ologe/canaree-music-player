package dev.olog.presentation.widgets

import android.content.Context
import android.util.AttributeSet
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.Target
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageView

open class PlayerImageView @JvmOverloads constructor(
        context: Context,
        attr: AttributeSet? = null

) : AdaptiveColorImageView(context, attr) {


    open fun loadImage(mediaId: MediaId){

        GlideApp.with(context).clear(this)

        GlideApp.with(context)
                .load(mediaId)
                .placeholder(CoverUtils.getGradient(context, mediaId))
                .priority(Priority.IMMEDIATE)
                .transition(DrawableTransitionOptions.withCrossFade())
                .override(Target.SIZE_ORIGINAL)
                .into(RippleTarget(this))
    }

}


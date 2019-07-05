package dev.olog.presentation.widgets.switcher

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ViewSwitcher
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.media.model.PlayerMetadata
import dev.olog.presentation.R
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageViewPresenter
import kotlin.properties.Delegates

class CustomViewSwitcher(
    context: Context,
    attrs: AttributeSet
) : ViewSwitcher(context, attrs), RequestListener<Drawable> {

    private val presenter by lazyFast { AdaptiveColorImageViewPresenter(context) }

    private enum class Direction {
        NONE,
        LEFT,
        RIGHT
    }

    private var currentDirection by Delegates.observable(Direction.NONE) { _, old, new ->
        if (old == new) {
            return@observable
        }

        val inAnim = when (new){
            Direction.RIGHT -> R.anim.slide_in_right
            Direction.LEFT -> R.anim.slide_in_left
            Direction.NONE -> android.R.anim.fade_in
        }
        val outAnim = when (new){
            Direction.RIGHT -> R.anim.slide_out_left
            Direction.LEFT -> R.anim.slide_out_right
            Direction.NONE -> android.R.anim.fade_out
        }
        setInAnimation(context, inAnim)
        setOutAnimation(context, outAnim)
    }

    fun loadImage(metadata: PlayerMetadata){
        currentDirection = when {
            metadata.isSkippingToNext -> Direction.RIGHT
            metadata.isSkippingToPrevious -> Direction.LEFT
            else -> Direction.NONE
        }
        loadImageInternal(metadata.mediaId)
    }

    private fun loadImageInternal(mediaId: MediaId){
        val nextImageView = (nextView as ViewGroup).findViewById(R.id.image) as ImageView

        GlideApp.with(context).clear(nextImageView)

        GlideApp.with(context)
            .load(mediaId)
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(Target.SIZE_ORIGINAL)
            .onlyRetrieveFromCache(true)
            .listener(this)
            .into(RippleTarget(nextImageView))
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        transitionToNext()
        if (model is MediaId){
            presenter.onNextImage(CoverUtils.getGradient(context, model))
        }
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        transitionToNext()
        presenter.onNextImage(resource)
        return false
    }

    private fun transitionToNext() = when (currentDirection){
        Direction.RIGHT -> showNext()
        Direction.LEFT -> showPrevious()
        Direction.NONE -> showNext()
    }

    fun observeProcessorColors() = presenter.observeProcessorColors()
    fun observePaletteColors() = presenter.observePalette()

    fun setChildrenActivated(activated: Boolean) {
        currentView.isActivated = activated
        nextView.isActivated = activated
    }
}
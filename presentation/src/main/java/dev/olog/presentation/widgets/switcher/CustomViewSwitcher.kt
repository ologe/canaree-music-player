package dev.olog.presentation.widgets.switcher

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.forEach
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
import dev.olog.presentation.widgets.imageview.AdaptiveImageHelper
import dev.olog.shared.extensions.findChild
import dev.olog.shared.extensions.lazyFast
import dev.olog.shared.theme.hasPlayerAppearance
import java.lang.IllegalStateException
import kotlin.properties.Delegates

class CustomViewSwitcher(
    context: Context,
    attrs: AttributeSet
) : MultiViewSwitcher(context, attrs), RequestListener<Drawable> {

    companion object {
        private val TAG = "P:${CustomViewSwitcher::class.java.simpleName}"
    }

    private var lastItem: MediaId? = null

    private val adaptiveImageHelper by lazyFast { AdaptiveImageHelper(context) }
    private val playerAppearance by lazyFast { context.hasPlayerAppearance() }

    private enum class Direction {
        NONE,
        LEFT,
        RIGHT
    }

    private var animationFinished = true

    private var currentDirection by Delegates.observable(Direction.NONE) { _, old, new ->
        if (old == new) {
            return@observable
        }

        val useExactPosition = playerAppearance.isBigImage() || playerAppearance.isFullscreen()

        val inAnim = when (new) {
            Direction.RIGHT -> {
                if (useExactPosition) {
                    R.anim.slide_in_right
                } else {
                    R.anim.slide_in_right_with_offset
                }
            }
            Direction.LEFT -> {
                if (useExactPosition) {
                    R.anim.slide_in_left
                } else {
                    R.anim.slide_in_left_with_offset
                }
            }
            Direction.NONE -> R.anim.fade_in
        }
        val outAnim = when (new) {
            Direction.RIGHT -> {
                if (useExactPosition) {
                    R.anim.slide_out_left
                } else {
                    R.anim.slide_out_left_with_offset
                }
            }
            Direction.LEFT -> {
                if (useExactPosition) {
                    R.anim.slide_out_right
                } else {
                    R.anim.slide_out_right_with_offset
                }
            }
            Direction.NONE -> R.anim.fade_out
        }
        setInAnimation(context, inAnim)
        setOutAnimation(context, outAnim)
    }

    fun loadImage(metadata: PlayerMetadata) {
        if (lastItem == metadata.mediaId) {
            return
        }
        lastItem = metadata.mediaId

        currentDirection = when {
            metadata.isSkippingToNext -> Direction.RIGHT
            metadata.isSkippingToPrevious -> Direction.LEFT
            else -> Direction.NONE
        }
        loadImageInternal(metadata.mediaId)
    }

    private fun loadImageInternal(mediaId: MediaId) {
        animationFinished = false

        val imageView = when (val next = getNextView()) {
            is ImageView -> next
            is ViewGroup -> next.findChild { it is ImageView }
            else -> throw IllegalStateException()
        } as ImageView

        GlideApp.with(context).clear(imageView)
        GlideApp.with(context)
            .load(mediaId)
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(Target.SIZE_ORIGINAL)
            .onlyRetrieveFromCache(true)
            .listener(this)
            .into(RippleTarget(imageView))
    }

    override fun onLoadFailed(
            e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        e?.printStackTrace()
        e?.logRootCauses(TAG)

        if (!animationFinished) {
            animationFinished = true
            showNext()

            if (model is MediaId) {
                adaptiveImageHelper.setImageDrawable(CoverUtils.getGradient(context, model))
            }
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
        if (!animationFinished) {
            animationFinished = true
            showNext()
        }
        adaptiveImageHelper.setImageDrawable(resource)
        return false
    }

    fun observeProcessorColors() = adaptiveImageHelper.observeProcessorColors()
    fun observePaletteColors() = adaptiveImageHelper.observePaletteColors()

    fun setChildrenActivated(activated: Boolean) {
        forEach {
            isActivated = activated
        }
    }
}
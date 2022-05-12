package dev.olog.ui.switcher

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.forEach
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.feature.media.model.PlayerMetadata
import dev.olog.platform.theme.hasPlayerAppearance
import dev.olog.ui.R
import dev.olog.shared.extension.findChild
import dev.olog.shared.extension.lazyFast
import dev.olog.ui.BlurredBackground
import dev.olog.ui.CoverUtils
import dev.olog.ui.RippleTarget
import dev.olog.ui.adaptive.AdaptiveImageHelper
import kotlin.properties.Delegates
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.ui.GlideUtils
import javax.inject.Inject

@AndroidEntryPoint
class CustomViewSwitcher(
    context: Context,
    attrs: AttributeSet
) : MultiViewSwitcher(context, attrs), RequestListener<Drawable> {

    companion object {
        @JvmStatic
        private val TAG = "P:${CustomViewSwitcher::class.java.simpleName}"
    }

    @Inject
    lateinit var requestManager: RequestManager

    private var lastItem: MediaId? = null

    private var imageVersion = 0

    private val blurBackground : BlurredBackground? by lazyFast {
        (parent as View).findViewById<BlurredBackground>(R.id.blurBackground)
    }

    private val adaptiveImageHelper by lazyFast {
        AdaptiveImageHelper(
            context
        )
    }
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

        imageVersion++
        val currentVersion = imageVersion

        val imageView = getImageView(getNextView())

        requestManager.clear(imageView)

        requestManager
            .load(mediaId)
            .placeholder(CoverUtils.onlyGradient(context, mediaId))
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(GlideUtils.OVERRIDE_BIG)
            .onlyRetrieveFromCache(true)
            .listener(this@CustomViewSwitcher)
            .into(RippleTarget(imageView)) // TODO ripple not working

        requestManager
            .load(mediaId)
            .priority(Priority.IMMEDIATE)
            .override(GlideUtils.OVERRIDE_BIG)
            .into(object : CustomTarget<Drawable>(){
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (resource !== imageView.drawable && currentVersion == imageVersion) {
                        // different image and same load
                        imageView.setImageDrawable(resource)
                        adaptiveImageHelper.setImageDrawable(resource)
                        blurBackground?.loadImage(mediaId, resource)
                    }
                }
            })
    }

    fun getImageView(parent: View = currentView): ImageView {
        return when (parent) {
            is ImageView -> parent
            is ViewGroup -> parent.findChild { it is ImageView }
            else -> throw IllegalStateException()
        } as ImageView
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
                val defaultCover = CoverUtils.getGradient(context, model)
                adaptiveImageHelper.setImageDrawable(defaultCover)
                blurBackground?.loadImage(model, defaultCover)
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
        blurBackground?.loadImage(model as MediaId, resource)
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
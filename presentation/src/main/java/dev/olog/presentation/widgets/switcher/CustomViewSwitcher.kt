package dev.olog.presentation.widgets.switcher

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.domain.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideUtils
import dev.olog.lib.media.model.PlayerMetadata
import dev.olog.presentation.R
import dev.olog.presentation.ripple.RippleTarget
import dev.olog.presentation.widgets.BlurredBackground
import dev.olog.shared.android.extensions.dip
import dev.olog.shared.android.extensions.findChild
import dev.olog.shared.android.theme.themeManager
import dev.olog.shared.lazyFast
import dev.olog.shared.widgets.adaptive.AdaptiveColorImageViewPresenter
import kotlin.properties.Delegates

class CustomViewSwitcher(
    context: Context,
    attrs: AttributeSet
) : MultiViewSwitcher(context, attrs), RequestListener<Drawable> {

    companion object {
        @JvmStatic
        private val TAG = "P:${CustomViewSwitcher::class.java.simpleName}"
    }

    private var showSpotifyIcon = false
    private val spotifyDrawable by lazyFast {
        ContextCompat.getDrawable(context, R.drawable.vd_spotify)!!
    }
    private val spotifyIconSize = context.dip(36)

    private var lastItem: MediaId? = null

    private var imageVersion = 0

    private val blurBackground : BlurredBackground? by lazyFast {
        (parent as View).findViewById<BlurredBackground>(R.id.blurBackground)
    }

    private val adaptiveImageHelper = AdaptiveColorImageViewPresenter(this)

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

        val playerAppearance = context.themeManager.playerAppearance

        // some player appearance are side to side
        val useExactPosition = playerAppearance.isBigImage || playerAppearance.isFullscreen

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
        if (metadata.mediaId.isSpotify) {
            loadSpotifyImage(metadata.mediaId)
        } else {
            loadImageInternal(metadata.mediaId)
        }
    }

    private fun loadSpotifyImage(mediaId: MediaId) {
        animationFinished = true

        imageVersion++
        val currentVersion = imageVersion

        val imageView = getImageView(currentView)
        GlideApp.with(context).clear(imageView)

        GlideApp.with(context)
            .load(CoverUtils.getGradient(context, mediaId))
            .into(imageView)

        loadRemote(imageView, mediaId, currentVersion)
        showSpotifyIcon = true
    }

    private fun loadImageInternal(mediaId: MediaId) {
        animationFinished = false

        imageVersion++
        val currentVersion = imageVersion

        val imageView = getImageView(getNextView())

        GlideApp.with(context).clear(imageView)

        loadCached(imageView, mediaId)
        loadRemote(imageView, mediaId, currentVersion)
        showSpotifyIcon = false
    }

    private fun loadCached(view: ImageView, mediaId: MediaId) {
        val thumbnail = GlideApp.with(context)
            .load(mediaId)
            .placeholder(CoverUtils.onlyGradient(context, mediaId))
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.IMMEDIATE)
            .override(GlideUtils.OVERRIDE_SMALL)
            .onlyRetrieveFromCache(true)

        GlideApp.with(context)
            .load(mediaId)
            .thumbnail(thumbnail)
            .placeholder(CoverUtils.onlyGradient(context, mediaId))
            .error(CoverUtils.getGradient(context, mediaId))
            .priority(Priority.HIGH)
            .override(GlideUtils.OVERRIDE_BIG)
            .onlyRetrieveFromCache(true)
            .listener(this)
            .into(RippleTarget(view)) // TODO ripple not working
    }

    /**
     * @param version is used to load only last image
     */
    private fun loadRemote(
        view: ImageView,
        mediaId: MediaId,
        version: Int
    ) {
        GlideApp.with(context)
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
                    if (resource !== view.drawable && version == imageVersion) {
                        // different image and same load
                        view.setImageDrawable(resource)
                        adaptiveImageHelper.onNextImage(resource)
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
                adaptiveImageHelper.onNextImage(defaultCover)
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
        adaptiveImageHelper.onNextImage(resource)
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (showSpotifyIcon) {
            // TODO not showing
            val width = getImageView(currentView).width
            val height = getImageView(currentView).height
            spotifyDrawable.setBounds(
                width - spotifyIconSize / 2 - spotifyIconSize,
                height - spotifyIconSize / 2 - spotifyIconSize,
                width - spotifyIconSize / 2,
                height -spotifyIconSize / 2
            )
            spotifyDrawable.draw(canvas)
        }
    }

}
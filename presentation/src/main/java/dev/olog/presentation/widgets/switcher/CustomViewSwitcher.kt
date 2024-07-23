package dev.olog.presentation.widgets.switcher

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ViewAnimator
import androidx.core.view.doOnLayout
import androidx.core.view.forEach
import dagger.hilt.android.AndroidEntryPoint
import dev.olog.core.MediaId
import dev.olog.image.provider.GlideApp
import dev.olog.media.model.PlayerMetadata
import dev.olog.presentation.R
import dev.olog.presentation.player.ui.animateElevation
import dev.olog.presentation.widgets.BlurredBackground
import dev.olog.presentation.widgets.imageview.AdaptiveImageHelper
import dev.olog.shared.android.extensions.findChild
import dev.olog.shared.android.theme.hasPlayerAppearance
import dev.olog.shared.android.theme.isBigImage
import dev.olog.shared.android.theme.isFullscreen
import dev.olog.shared.android.viewScope
import dev.olog.shared.lazyFast
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@AndroidEntryPoint
class CustomViewSwitcher : ViewAnimator {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var lastItem: MediaId? = null

    private val blurBackground : BlurredBackground? by lazyFast {
        (parent as View).findViewById<BlurredBackground>(R.id.blurBackground)
    }

    @Inject
    lateinit var adaptiveImageHelper: AdaptiveImageHelper
    private val hasPlayerAppearance by lazyFast { context.hasPlayerAppearance() }

    private enum class Direction {
        NONE,
        LEFT,
        RIGHT
    }

    private var currentDirection by Delegates.observable(Direction.NONE) { _, old, new ->
        if (old == new) {
            return@observable
        }

        val playerAppearance = hasPlayerAppearance.playerAppearance()
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
        val isFirstLoad = lastItem == null
        if (lastItem == metadata.mediaId) {
            return
        }
        lastItem = metadata.mediaId

        currentDirection = when {
            metadata.isSkippingToNext -> Direction.RIGHT
            metadata.isSkippingToPrevious -> Direction.LEFT
            else -> Direction.NONE
        }
        loadImageInternal(metadata.mediaId, isFirstLoad)
    }

    private fun loadImageInternal(
        mediaId: MediaId,
        isFirstLoad: Boolean,
    ) {
        if (isFirstLoad) {
            loadFirstImage(mediaId)
            return
        }

        val imageView = getImageView(getNextView())

        GlideApp.with(context).clear(imageView)

        imageView.loadImageJob = imageView.viewScope.launch {
            showNext()
            context.loadPlayerCachedImage(mediaId)
                .collect {
                    imageView.setImageDrawable(it)
                }
            updateDecorations(imageView, mediaId, imageView.drawable)

            context.loadPlayerImage(mediaId, false)
                .collect {
                    imageView.setImageDrawable(it)
                }
            updateDecorations(imageView, mediaId, imageView.drawable)
        }
    }

    private fun loadFirstImage(mediaId: MediaId) {
        val imageView = getImageView(currentView)

        imageView.loadImageJob = imageView.viewScope.launch {
            context.loadPlayerImage(mediaId, true)
                .collect {
                    imageView.setImageDrawable(it)
                }
            updateDecorations(imageView, mediaId, imageView.drawable)
        }
    }

    fun getImageView(parent: View = currentView): ImageView {
        return when (parent) {
            is ImageView -> parent
            is ViewGroup -> parent.findChild { it is ImageView }
            else -> throw IllegalStateException()
        } as ImageView
    }

    private fun updateDecorations(
        imageView: ImageView,
        mediaId: MediaId,
        drawable: Drawable?
    ) {
        if (mediaId == lastItem) {
            imageView.doOnLayout {
                adaptiveImageHelper.setImageDrawable(drawable)
                blurBackground?.loadImage(mediaId, drawable)
            }
        }
    }

    fun updateChildren(isPlaying: Boolean) {
        forEach {
            it.animateElevation(isPlaying)
        }
    }
}

private fun ViewAnimator.getNextView(): View {
    var nextChild = displayedChild + 1
    if (nextChild >= childCount) {
        nextChild = 0
    }
    return getChildAt(nextChild)
}

private var ImageView.loadImageJob: Job?
    get() = getTag(R.id.player_load_image_job) as? Job?
    set(value) {
        (getTag(R.id.player_load_image_job) as? Job?)?.cancel()
        setTag(R.id.player_load_image_job, value)
    }
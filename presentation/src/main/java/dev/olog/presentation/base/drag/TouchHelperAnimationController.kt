package dev.olog.presentation.base.drag

import android.graphics.Color
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.platform.extension.setVisible
import dev.olog.platform.extension.toggleVisibility
import dev.olog.presentation.R
import dev.olog.shared.lazyFast
import dev.olog.ui.palette.colorControlNormal
import dev.olog.ui.palette.colorSwipeBackground
import kotlin.math.hypot

internal class TouchHelperAnimationController {

    companion object {
        private const val DELETE_COLOR = 0xff_cf1721.toInt()
        private const val PLAY_NEXT_COLOR = 0xff_364854.toInt()
    }

    enum class State {
        IDLE, SWIPE_LEFT, SWIPE_RIGHT, CIRCULAR_REVEAL
    }

    private var state = State.IDLE

    fun setAnimationIdle() {
        state = State.IDLE
    }

    private val decelerateInterpolator by lazyFast { DecelerateInterpolator() }
    private val accelerateInterpolator by lazyFast { AccelerateInterpolator() }
    private val bounceInterpolator by lazyFast { BounceInterpolator() }

    fun initializeSwipe(
        viewHolder: RecyclerView.ViewHolder,
        dx: Float
    ) {
        if (dx > 0) {
            if (state == State.SWIPE_RIGHT) {
                return
            }
            state = State.SWIPE_RIGHT
        }
        if (dx < 0) {
            if (state == State.SWIPE_LEFT) {
                return
            }
            state = State.SWIPE_LEFT
        }

        val delete = viewHolder.itemView.findViewById<ImageView>(R.id.deleteIcon)
        val playNext = viewHolder.itemView.findViewById<ImageView>(R.id.playNextIcon)
        val background = viewHolder.itemView.findViewById<View>(R.id.background)
        delete?.let {
            val buttonColor = it.context.colorControlNormal()
            it.setColorFilter(buttonColor)
        }
        playNext?.let {
            val buttonColor = it.context.colorControlNormal()
            it.setColorFilter(buttonColor)
        }
        background?.let {
            val backgroundColor = it.context.colorSwipeBackground()
            it.setBackgroundColor(backgroundColor)
        }
        delete?.toggleVisibility(dx > 0, false)
        playNext?.toggleVisibility(dx < 0, false)
    }

    fun drawCircularReveal(
        viewHolder: RecyclerView.ViewHolder,
        dx: Float
    ) {
        if (state == State.CIRCULAR_REVEAL) {
            return
        }
        state = State.CIRCULAR_REVEAL

        val mainIcon =
            viewHolder.itemView.findViewById<ImageView>(if (dx > 0f) R.id.deleteIcon else R.id.playNextIcon)
        val background = viewHolder.itemView.findViewById<View>(R.id.background)

        if (mainIcon == null || background == null) {
            return
        }

        val w = background.width
        val h = background.height
        val endRadius = hypot(w.toDouble(), h.toDouble()).toFloat()

        val cx = mainIcon.x + mainIcon.width / 2
        val cy = mainIcon.y + mainIcon.height / 2

        val anim = ViewAnimationUtils.createCircularReveal(
            background, cx.toInt(), cy.toInt(),
            0f, endRadius
        )
        background.setVisible()
        anim.duration = 400
        anim.interpolator = accelerateInterpolator
        anim.start()

        mainIcon.setColorFilter(Color.WHITE)
        background.setBackgroundColor(if (dx > 0) DELETE_COLOR else PLAY_NEXT_COLOR)

        mainIcon.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(200)
            .setInterpolator(decelerateInterpolator)
            .withEndAction {
                mainIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(bounceInterpolator)
            }
    }

}
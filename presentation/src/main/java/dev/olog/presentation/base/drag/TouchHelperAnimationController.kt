package dev.olog.presentation.base.drag

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R
import dev.olog.shared.extensions.*
import kotlin.math.hypot

internal class TouchHelperAnimationController {

    companion object {
        private val TAG = "P:${TouchHelperAnimationController::class.java.simpleName}"
    }

    enum class State {
        IDLE, SWIPE_LEFT, SWIPE_RIGHT, CIRCULAR_REVEAL
    }

    private var state = State.IDLE

    fun setAnimationIdle() {
        state = State.IDLE
    }

    private val interpolator by lazyFast { DecelerateInterpolator() }
    private val bounce by lazyFast { BounceInterpolator() }

    fun initializeSwipe(
        viewHolder: RecyclerView.ViewHolder,
        dx: Float
    ) {
        val oldState = state
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
        anim.duration = 600
        anim.start()

        mainIcon.setColorFilter(Color.WHITE)
        background.setBackgroundColor(if (dx > 0) 0xff_ff4444.toInt() else 0xff_364854.toInt())

        mainIcon.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(250)
            .setInterpolator(interpolator)
            .withEndAction {
                mainIcon.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(250)
                    .setInterpolator(bounce)
            }
    }

}
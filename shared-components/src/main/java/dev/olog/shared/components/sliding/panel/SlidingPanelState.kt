package dev.olog.shared.components.sliding.panel

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableConstants
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.savedinstancestate.Saver
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.platform.AnimationClockAmbient

enum class SlidingPanelValue {
    Collapsed, Expanded
}

@OptIn(ExperimentalMaterialApi::class)
class SlidingPanelState(
    initialValue: SlidingPanelValue,
    clock: AnimationClockObservable,
    confirmStateChange: (SlidingPanelValue) -> Boolean = { true }
) : SwipeableState<SlidingPanelValue>(
    initialValue = initialValue,
    clock = clock,
    animationSpec = SwipeableConstants.DefaultAnimationSpec, // TODO improve animation?
    confirmStateChange = confirmStateChange
) {

    // scroll distance
    internal var delta: Float = Float.NaN

    val fraction: Float
        get() {
            if (delta.isNaN()) {
                return when {
                    isExpanded -> 1f
                    else -> 0f
                }
            }
            return 1 - offset.value / delta
        }

    val isExpanded: Boolean
        get() = value == SlidingPanelValue.Expanded

    val isCollapsed: Boolean
        get() = value == SlidingPanelValue.Collapsed

    fun expand(onOpened: (() -> Unit)? = null) {
        animateTo(SlidingPanelValue.Expanded, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == SlidingPanelValue.Expanded
            ) {
                onOpened?.invoke()
            }
        })
    }

    fun collapse(onClosed: (() -> Unit)? = null) {
        animateTo(SlidingPanelValue.Collapsed, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == SlidingPanelValue.Collapsed
            ) {
                onClosed?.invoke()
            }
        })
    }

    companion object {

        @Suppress("FunctionName")
        fun Saver(
            clock: AnimationClockObservable,
            confirmStateChange: (SlidingPanelValue) -> Boolean
        ) = Saver<SlidingPanelState, SlidingPanelValue>(
            save = { it.value },
            restore = { SlidingPanelState(it, clock, confirmStateChange) }
        )
    }

}

@Composable
fun rememberSlidingPanelState(
    initialValue: SlidingPanelValue = SlidingPanelValue.Collapsed,
    confirmStateChange: (SlidingPanelValue) -> Boolean = { true }
): SlidingPanelState {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    return rememberSavedInstanceState(
        clock,
        saver = SlidingPanelState.Saver(clock, confirmStateChange)
    ) {
        SlidingPanelState(initialValue, clock, confirmStateChange)
    }
}
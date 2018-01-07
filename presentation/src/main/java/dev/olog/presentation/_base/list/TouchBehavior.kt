package dev.olog.presentation._base.list

import android.support.annotation.LayoutRes

interface TouchBehavior {

    companion object {
        val UNSET = 0
    }

}

class TouchCallbackConfig(
        val canDrag: Boolean = false,
        val canSwipe: Boolean = false,
        @LayoutRes val draggableViewType: Int = TouchBehavior.UNSET,
        val onDragAction: (from: Int, to: Int) -> Unit = { _, _ ->  }, // do nothing
        val onSwipeAction: (position: Int) -> Unit = { _ ->  } // do nothing
)
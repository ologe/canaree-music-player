package dev.olog.presentation._base.list

import android.support.annotation.LayoutRes

class TouchCallbackConfig(
        val canDrag: Boolean = false,
        val canSwipe: Boolean = false,
        @LayoutRes val draggableViewType: Int = 0,
        val onDragAction: (from: Int, to: Int) -> Unit = { _, _ ->  }, // do nothing
        val onSwipeAction: (position: Int) -> Unit = { _ ->  } // do nothing
)
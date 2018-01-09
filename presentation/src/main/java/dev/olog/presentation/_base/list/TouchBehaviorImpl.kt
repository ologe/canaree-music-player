package dev.olog.presentation._base.list

import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback

class TouchBehaviorImpl(
        private val dataController: TouchBehaviorCapabilities,
        private val touchCallbackConfig: TouchCallbackConfig,
        canSwipe: Boolean

) : ItemTouchHelperAdapter {

    override val draggableViewType: Int = touchCallbackConfig.draggableViewType

    init {
        if (draggableViewType == TouchBehavior.UNSET) {
            throw IllegalStateException("try with a valid viewType")
        }
    }

    val callback = ItemTouchHelperCallback(this, canSwipe)
    val touchHelper = ItemTouchHelper(callback)

    override fun onItemMove(from: Int, to: Int) {
        val (realFrom, realTo) = dataController.swap(from, to)
        val headers = dataController.headersWithinList(from, draggableViewType)
        touchCallbackConfig.onDragAction.invoke(
                realFrom - headers,
                realTo - headers)
    }

    override fun onItemDismiss(position: Int) {
        touchCallbackConfig.onSwipeAction.invoke(position)
        dataController.remove(position)
    }
}
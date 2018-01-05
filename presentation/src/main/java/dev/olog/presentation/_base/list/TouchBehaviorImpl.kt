package dev.olog.presentation._base.list

import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback

class TouchBehaviorImpl(
        private val dataController: TouchBehaviorCapabilities,
        private val touchCallbackConfig: TouchCallbackConfig

) : ItemTouchHelperAdapter {

    override val draggableViewType: Int = touchCallbackConfig.draggableViewType

    init {
        if (draggableViewType == TouchBehavior.UNSET) {
            throw IllegalStateException("try with a valid viewType")
        }
    }

    val callback = ItemTouchHelperCallback(this)
    val touchHelper = ItemTouchHelper(callback)

    override fun onItemMove(from: Int, to: Int) {
        dataController.swap(from, to)
        val headers = dataController.headersWithinList(from, draggableViewType)
        val realFromPosition = from - headers
        val realToPosition = to - headers
        touchCallbackConfig.onDragAction.invoke(realFromPosition, realToPosition)
    }

    override fun onItemDismiss(position: Int) {
        dataController.remove(position)
        val headers = dataController.headersWithinList(position, draggableViewType)
        val realPosition = position - headers
        touchCallbackConfig.onSwipeAction.invoke(realPosition)
    }
}
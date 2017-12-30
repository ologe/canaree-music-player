package dev.olog.presentation._base

import android.support.annotation.LayoutRes
import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback

class DraggableBehaviorImpl (
        private val dataController: DraggableControllerCapabilities,
        @LayoutRes private val draggableViewType: Int

) : ItemTouchHelperAdapter {

    init {
        if (draggableViewType == DraggableBehavior.UNSET) {
            throw IllegalStateException("try with a valid viewType")
        }
    }

    val callback = ItemTouchHelperCallback(this)
    val touchHelper = ItemTouchHelper(callback)

    override fun onItemMove(from: Int, to: Int) {
        dataController.swap(from, to)
    }

    override fun onItemDismiss(position: Int) {
        dataController.remove(position)
    }

    override fun isViewTypeDraggable(): Int = draggableViewType
}
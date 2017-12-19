package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter

abstract class BaseListAdapterDraggable<Model> (
        lifecycle: Lifecycle

) : BaseListAdapter<Model>(lifecycle), ItemTouchHelperAdapter {

    var touchHelper : ItemTouchHelper? = null

    override fun onItemMove(from: Int, to: Int) {
        dataController.swap(from, to)
    }

    override fun onItemDismiss(position: Int) {
        dataController.remove(position)
    }
}
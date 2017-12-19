package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter

abstract class BaseMapAdapterDraggable<E: Enum<E>, Model> (
        lifecycle: Lifecycle,
        enums: Array<E>

): BaseMapAdapter<E, Model>(lifecycle, enums), ItemTouchHelperAdapter {

    var touchHelper : ItemTouchHelper? = null

    override fun onItemMove(from: Int, to: Int) {
        dataController.swap(from, to)
    }

    override fun onItemDismiss(position: Int) {

    }

    override fun isSwipeEnabled(): Boolean = false

}
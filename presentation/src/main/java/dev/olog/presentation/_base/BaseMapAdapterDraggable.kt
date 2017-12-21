package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.support.annotation.CallSuper
import android.support.v7.widget.helper.ItemTouchHelper
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter

abstract class BaseMapAdapterDraggable<E: Enum<E>, Model> (
        lifecycle: Lifecycle,
        enums: Array<E>

): BaseMapAdapter<E, Model>(lifecycle, enums), ItemTouchHelperAdapter {

    var touchHelper : ItemTouchHelper? = null

    @CallSuper
    override fun onItemMove(from: Int, to: Int) {
        dataController.swap(from, to)
    }

    @CallSuper
    override fun onItemDismiss(position: Int) {

    }

    override fun isSwipeEnabled(): Boolean = false

}
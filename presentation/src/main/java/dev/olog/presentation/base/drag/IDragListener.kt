package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import java.lang.IllegalStateException


interface IDragListener {
    var itemTouchHelper: ItemTouchHelper?

    fun setupDragListener(scope: CoroutineScope, list: RecyclerView, direction: Int)
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

class DragListenerImpl : IDragListener {
    override var itemTouchHelper: ItemTouchHelper? = null

    override fun setupDragListener(
        scope: CoroutineScope,
        list: RecyclerView,
        direction: Int
    ) {
        val adapter = list.adapter ?: throw IllegalStateException("list must have a nonnull adapter")

        if (adapter !is TouchableAdapter){
            throw IllegalStateException("${adapter::class.java.name} must implement ${TouchableAdapter::class.java.name}'")
        }
        val callback = TouchHelperAdapterCallback(scope, adapter, direction)
        itemTouchHelper = ItemTouchHelper(callback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}
package dev.olog.platform.adapter.drag

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope


interface IDragListener {
    var itemTouchHelper: ItemTouchHelper?

    fun setupDragListener(
        scope: CoroutineScope,
        list: RecyclerView,
        direction: Int,
        animation: TouchHelperAdapterAnimation,
    )
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

class DragListenerImpl : IDragListener {
    override var itemTouchHelper: ItemTouchHelper? = null

    override fun setupDragListener(
        scope: CoroutineScope,
        list: RecyclerView,
        direction: Int,
        animation: TouchHelperAdapterAnimation,
    ) {
        val adapter = list.adapter ?: throw IllegalStateException("list must have a nonnull adapter")

        if (adapter !is TouchableAdapter){
            throw IllegalStateException("${adapter::class.java.name} must implement ${TouchableAdapter::class.java.name}'")
        }
        val callback = TouchHelperAdapterCallback(scope, adapter, direction, animation)
        itemTouchHelper = ItemTouchHelper(callback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}
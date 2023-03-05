package dev.olog.presentation.base.drag

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException


interface IDragListener {
    var itemTouchHelper: ItemTouchHelper?

    fun setupDragListener(lifecycleOwner: LifecycleOwner, list: RecyclerView, direction: Int)
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}

class DragListenerImpl : IDragListener {
    override var itemTouchHelper: ItemTouchHelper? = null

    override fun setupDragListener(
        lifecycleOwner: LifecycleOwner,
        list: RecyclerView,
        direction: Int
    ){
        val adapter = list.adapter ?: throw IllegalStateException("list must have a nonnull adapter")

        if (adapter !is TouchableAdapter){
            throw IllegalStateException("${adapter::class.java.name} must implement ${TouchableAdapter::class.java.name}'")
        }
        val callback = TouchHelperAdapterCallback(lifecycleOwner, adapter, direction)
        itemTouchHelper = ItemTouchHelper(callback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}
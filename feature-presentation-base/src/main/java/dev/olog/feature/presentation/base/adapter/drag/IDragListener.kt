package dev.olog.feature.presentation.base.adapter.drag

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException


interface IDragListener {
    var itemTouchHelper: ItemTouchHelper?

    fun setupDragListener(list: RecyclerView, direction: Int)
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    fun disposeDragListener()
}

class DragListenerImpl :
    IDragListener {

    override var itemTouchHelper: ItemTouchHelper? = null

    override fun setupDragListener(list: RecyclerView, direction: Int){
        val adapter = list.adapter ?: throw IllegalStateException("list must have a nonnull adapter")

        if (adapter !is TouchableAdapter){
            throw IllegalStateException("${adapter::class.java.name} must implement ${TouchableAdapter::class.java.name}'")
        }
        val callback = TouchHelperAdapterCallback(adapter, direction)
        itemTouchHelper = ItemTouchHelper(callback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun disposeDragListener() {
        itemTouchHelper = null
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }
}
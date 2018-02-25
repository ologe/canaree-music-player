package dev.olog.msc.presentation.base.adp

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

class TouchHelperAdapterCallback(
        private val adapter : TouchableAdapter

) : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        ItemTouchHelper.END or ItemTouchHelper.START

) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!!){
            adapter.onMoved(viewHolder.adapterPosition, target.adapterPosition)
        }
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (adapter.canInteractWithViewHolder(viewHolder.itemViewType)!!){
            adapter.onSwiped(viewHolder.adapterPosition)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

}
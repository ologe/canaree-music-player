package dev.olog.msc.presentation.base.adapter.drag

import android.support.v7.widget.RecyclerView

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder)
    fun onSwipedRight(position: Int)
    fun canInteractWithViewHolder(viewType: Int): Boolean?

}
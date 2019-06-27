package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder)
    fun onSwipedRight(viewHolder: RecyclerView.ViewHolder)
    fun canInteractWithViewHolder(viewType: Int): Boolean?
    fun onClearView()

}
package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView

interface TouchableAdapter {

    fun canInteractWithViewHolder(viewType: Int): Boolean

    fun onMoved(from: Int, to: Int) {}

    fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {}
    fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {}

    fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {}
    fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {}

    fun onClearView() {}

}
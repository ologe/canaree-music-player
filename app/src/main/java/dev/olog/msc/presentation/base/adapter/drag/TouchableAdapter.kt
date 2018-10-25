package dev.olog.msc.presentation.base.adapter.drag

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwipedLeft(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
    fun onSwipedRight(position: Int)
    fun canInteractWithViewHolder(viewType: Int): Boolean?
    fun onClearView()

}
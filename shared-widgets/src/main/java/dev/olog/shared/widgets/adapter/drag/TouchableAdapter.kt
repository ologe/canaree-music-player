package dev.olog.shared.widgets.adapter.drag

import androidx.recyclerview.widget.RecyclerView

interface TouchableAdapter {

    fun canInteractWithViewHolder(viewType: Int): Boolean

    fun onMoved(from: Int, to: Int) {}

    /**
     * perform business logic
     */
    fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {}
    /**
     * perform recycler view animation
     */
    fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {}

    /**
     * perform business logic
     */
    fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {}

    /**
     * perform recycler view animation
     */
    fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {}

    fun onClearView() {}

}
package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView.ViewHolder

interface TouchableAdapter {

    fun canInteractWithViewHolder(viewHolder: ViewHolder): Boolean

    fun onMoved(from: Int, to: Int) {}

    /**
     * perform business logic
     */
    fun onSwipedLeft(viewHolder: ViewHolder) {}
    /**
     * perform recycler view animation
     */
    fun afterSwipeLeft(viewHolder: ViewHolder) {}

    /**
     * perform business logic
     */
    fun onSwipedRight(viewHolder: ViewHolder) {}

    /**
     * perform recycler view animation
     */
    fun afterSwipeRight(viewHolder: ViewHolder) {}

    fun onClearView() {}

}
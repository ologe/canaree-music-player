package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView
import dev.olog.presentation.R

interface TouchableAdapter {

    fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.compose_interop_swipeable
    }

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
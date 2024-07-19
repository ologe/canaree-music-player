package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.olog.presentation.R

interface TouchableAdapter {

    @Deprecated("make default after migration")
    fun canInteractWithViewHolder(viewHolder: ViewHolder): Boolean {
        return viewHolder.itemViewType == R.layout.item_swipeable_compose
    }

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
package dev.olog.presentation.utils.recycler_view

interface ItemTouchHelperAdapter {

    fun onItemMove(from: Int, to: Int)

    fun onItemDismiss(position: Int)

    val draggableViewType: Int

    val isSwipeEnabled: Boolean

}
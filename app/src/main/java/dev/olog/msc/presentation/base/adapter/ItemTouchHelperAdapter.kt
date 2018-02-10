package dev.olog.msc.presentation.base.adapter

interface ItemTouchHelperAdapter {

    fun onItemMove(from: Int, to: Int)

    fun onItemDismiss(position: Int)

    val draggableViewType: Int

}
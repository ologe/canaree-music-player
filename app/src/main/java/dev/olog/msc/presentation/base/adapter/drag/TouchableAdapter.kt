package dev.olog.msc.presentation.base.adapter.drag

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwiped(position: Int)
    fun onInteractionEnd(position: Int)
    fun canInteractWithViewHolder(viewType: Int): Boolean?

}
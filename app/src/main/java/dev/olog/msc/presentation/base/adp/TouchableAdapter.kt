package dev.olog.msc.presentation.base.adp

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwiped(position: Int)
    fun canInteractWithViewHolder(viewType: Int): Boolean?

}
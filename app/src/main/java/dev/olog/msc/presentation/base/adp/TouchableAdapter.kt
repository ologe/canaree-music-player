package dev.olog.msc.presentation.base.adp

import android.support.v7.widget.RecyclerView

interface TouchableAdapter {

    fun onMoved(from: Int, to: Int)
    fun onSwiped(position: Int)
    fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean?

}
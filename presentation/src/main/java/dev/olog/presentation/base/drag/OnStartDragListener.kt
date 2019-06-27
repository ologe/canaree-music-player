package dev.olog.presentation.base.drag

import androidx.recyclerview.widget.RecyclerView


interface OnStartDragListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
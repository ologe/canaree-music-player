package dev.olog.presentation._base

import android.support.v7.widget.RecyclerView

interface DraggableBehavior {

    companion object {
        val UNSET = 0
    }

    fun startDrag(viewHolder: RecyclerView.ViewHolder)

}
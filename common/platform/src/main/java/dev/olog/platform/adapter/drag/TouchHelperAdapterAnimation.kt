package dev.olog.platform.adapter.drag

import androidx.recyclerview.widget.RecyclerView

interface TouchHelperAdapterAnimation {

    fun onSwipe(
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float
    )

}
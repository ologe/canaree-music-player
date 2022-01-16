package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.RecyclerView

// TODO bad name
abstract class OptionalAdapter<VH : RecyclerView.ViewHolder>(
    show: Boolean = false,
) : RecyclerView.Adapter<VH>() {

    var show: Boolean = show
        set(value) {
            val displayOldItem = field
            val displayNewItem = value

            if (displayOldItem && !displayNewItem) {
                notifyItemRemoved(0)
            } else if (displayNewItem && !displayOldItem) {
                notifyItemInserted(0)
            } else if (displayOldItem && displayNewItem) {
                notifyItemChanged(0)
            }

            field = value
        }

    final override fun getItemCount(): Int = if (show) 1 else 0

}
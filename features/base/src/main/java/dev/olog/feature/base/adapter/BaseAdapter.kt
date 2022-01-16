package dev.olog.feature.base.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.base.anim.ScaleMoreInOnTouch

abstract class BaseAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    differ: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(differ),
    Submittable<T> {

    final override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: return
        onBindViewHolder(holder, item, position)
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        val item: T? = payloads.firstOrNull() as? T?
        if (item == null) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            onBindViewHolder(holder, item, position)
        }
    }

    protected abstract fun onBindViewHolder(holder: VH, item: T, position: Int)

    protected fun VH.setupDefaultClickListeners(
        onClick: (T, Int) -> Unit,
        onLongClick: (T, Int) -> Unit,
    ) {
        itemView.setOnClickListener {
            val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnClickListener
            val item = getItem(position) ?: return@setOnClickListener
            onClick(item, position)
        }

        itemView.setOnLongClickListener {
            val position = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return@setOnLongClickListener false
            val item = getItem(position) ?: return@setOnLongClickListener false
            onLongClick(item, position)
            true
        }
    }

    protected fun VH.setupElevateOnTouch() {
        itemView.setOnTouchListener(ScaleMoreInOnTouch(itemView))
    }

}
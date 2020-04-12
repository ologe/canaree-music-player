package dev.olog.feature.presentation.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import dev.olog.feature.presentation.base.CustomListAdapter

private class SimpleItemCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

abstract class SimpleAdapter<T: Any> : CustomListAdapter<T, DataBoundViewHolder>(SimpleItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflater.inflate(viewType, parent, false)
        val viewHolder =
            DataBoundViewHolder(
                binding
            )
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    fun getData(): List<T> = currentList

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = getItem(position)
        bind(holder, item, position)
    }

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

    @CallSuper
    override fun onViewAttachedToWindow(holder: DataBoundViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAppear()
    }

    @CallSuper
    override fun onViewDetachedFromWindow(holder: DataBoundViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDisappear()
    }

    abstract override fun getItemViewType(position: Int): Int

}
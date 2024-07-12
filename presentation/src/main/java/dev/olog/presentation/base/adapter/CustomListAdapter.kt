package dev.olog.presentation.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class CustomListAdapter<T : Any, B : ViewBinding>(
    diffCallback: DiffUtil.ItemCallback<T> = DefaultDiffCallback()
) : ListAdapter<T, CustomViewHolder<B>>(diffCallback) {

    class DefaultDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

    abstract fun inflate(inflater: LayoutInflater, parent: ViewGroup): B

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflate(inflater, parent)
        val viewHolder = CustomViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    override fun onBindViewHolder(holder: CustomViewHolder<B>, position: Int) {
        bind(holder, getItem(position), position)
    }

    abstract fun initViewHolderListeners(viewHolder: CustomViewHolder<B>, viewType: Int)

    abstract fun bind(holder: CustomViewHolder<B>, item: T, position: Int)

    public override fun getItem(position: Int): T {
        return super.getItem(position)
    }

}

class CustomViewHolder<T : ViewBinding>(
    val binding: T
) : RecyclerView.ViewHolder(binding.root)
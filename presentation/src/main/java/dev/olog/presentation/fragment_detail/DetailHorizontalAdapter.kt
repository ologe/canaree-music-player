package dev.olog.presentation.fragment_detail

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.BR
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.model.DisplayableItem

class DetailHorizontalAdapter(
        private val data: List<DisplayableItem>

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        return DataBoundViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, data[position], position)
        holder.binding.executePendingBindings()
    }

    private fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  2)
        binding.setVariable(BR.position, position)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].type

}
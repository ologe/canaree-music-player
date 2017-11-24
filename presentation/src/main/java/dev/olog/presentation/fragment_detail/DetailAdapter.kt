package dev.olog.presentation.fragment_detail

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.DataBoundViewHolder
import dev.olog.presentation.dagger.FragmentLifecycle
import dev.olog.presentation.model.DisplayableItem
import javax.inject.Inject

class DetailAdapter @Inject constructor(
        @FragmentLifecycle lifecycle: Lifecycle
) :RecyclerView.Adapter<DataBoundViewHolder<*>>(), DefaultLifecycleObserver {

    private val allData = DetailData()
    val innerAdapter = DetailHorizontalAdapter(allData.songs)
    val recycled = RecyclerView.RecycledViewPool()

    init {
        lifecycle.addObserver(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int){
        if (viewType == R.layout.item_horizontal_list) {
            val list = viewHolder.itemView as RecyclerView
            list.layoutManager = GridLayoutManager(viewHolder.itemView.context,
                    5, GridLayoutManager.HORIZONTAL, false)
            list.adapter = innerAdapter
            list.recycledViewPool = recycled

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(list)
        }
    }

    override fun getItemCount(): Int = allData.getSize()

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, allData[position], position)
        holder.binding.executePendingBindings()
    }

    private fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int){
        val source = if(item.type == R.layout.item_detail_album) 2 else 3
        binding.setVariable(BR.item, item)
        binding.setVariable(BR.source,  source)
        binding.setVariable(BR.position, position)
    }

    override fun getItemViewType(position: Int): Int = allData[position].type

    fun getItem(position: Int): DisplayableItem = allData[position]

}
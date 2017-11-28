package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.Header

abstract class BaseAdapter (
        lifecycle: Lifecycle

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    private val dataController = BaseAdapterController(this)

    init {
        lifecycle.addObserver(dataController)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, dataController[position], position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding,
                                item: DisplayableItem,
                                position: Int)

    fun updateDataSet(dataSet: List<DisplayableItem>) {
        dataController.updateData(dataSet)
    }

    override fun getItemCount(): Int = dataController.getSize()

    override fun getItemViewType(position: Int): Int = dataController[position].type

    internal fun getDataSet(): List<DisplayableItem> = dataController.dataSet

    open fun provideHeaders() : List<Header> = listOf()

    fun onDataChanged() = dataController.onDataChanged

    open fun hasGranularUpdate() = false

}

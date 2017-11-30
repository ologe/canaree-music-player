package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseMapAdapter<E: Enum<E>, Model> (
        lifecycle: Lifecycle,
        enums: Array<E>

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    protected val dataController = BaseMapAdapterController(this, enums)

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

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    override fun getItemCount(): Int = dataController.getSize()

    override abstract fun getItemViewType(position: Int): Int

    abstract fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean

    fun updateDataSet(data: MutableMap<E, MutableList<Model>>) {
        dataController.onNext(data)
    }

    fun getItemAt(position: Int): Model = dataController[position]

    internal open fun afterDataChanged(){
    }

}

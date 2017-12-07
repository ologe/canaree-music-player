package dev.olog.presentation._base

import android.arch.lifecycle.Lifecycle
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.Flowable

abstract class BaseListAdapter<Model> (
        lifecycle: Lifecycle

) : RecyclerView.Adapter<DataBoundViewHolder<*>>() {

    protected val dataController = BaseListAdapterController(this)

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

    fun updateDataSet(dataSet: List<Model>) {
        dataController.onNext(dataSet)
    }

    override fun getItemCount(): Int = dataController.getSize()

    override abstract fun getItemViewType(position: Int): Int

    internal fun getDataSet(): List<Model> = dataController.getDataSet()

    internal fun getItem(position: Int): Model = dataController[position]

    fun onDataChanged() : Flowable<List<Model>> = dataController.onDataChanged()

    open val hasGranularUpdate : Boolean = true

    abstract fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean

    internal open fun afterDataChanged(){
    }
}

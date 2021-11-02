package dev.olog.feature.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleAdapter<T>(
    protected val dataSet: MutableList<T>
) : RecyclerView.Adapter<DataBoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflater.inflate(viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    fun getData(): List<T> = dataSet.toList()

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int)

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        val item = dataSet[position]
        bind(holder, item, position)
    }

    protected abstract fun bind(holder: DataBoundViewHolder, item: T, position: Int)

    fun getItem(position: Int): T? {
        if (position in 0..dataSet.size) {
            return dataSet[position]
        }
        return null
    }

    fun updateDataSet(data: List<T>) {
        this.dataSet.clear()
        this.dataSet.addAll(data)
        notifyDataSetChanged()
    }

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
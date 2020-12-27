package dev.olog.shared.widgets.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class SimpleAdapter<T>(
    protected val dataSet: MutableList<T>
) : RecyclerView.Adapter<LayoutContainerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutContainerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflater.inflate(viewType, parent, false)
        val viewHolder = LayoutContainerViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    fun getData(): List<T> = dataSet.toList()

    protected abstract fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int)

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: LayoutContainerViewHolder, position: Int) {
        val item = dataSet[position]

        holder.bind(item, position)
    }

    protected abstract fun LayoutContainerViewHolder.bind(item: T, position: Int)

    fun getItem(position: Int): T = dataSet[position]

    fun updateDataSet(data: List<T>) {
        this.dataSet.clear()
        this.dataSet.addAll(data)
        notifyDataSetChanged()
    }

    abstract override fun getItemViewType(position: Int): Int

}
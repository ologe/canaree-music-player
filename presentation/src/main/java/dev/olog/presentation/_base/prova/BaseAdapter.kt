package dev.olog.presentation._base.prova

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.ViewGroup
import dev.olog.presentation._base.BaseModel
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import dev.olog.shared.clearThenAdd
import dev.olog.shared.swap

abstract class BaseAdapter <in Model: BaseModel> (


) : RecyclerView.Adapter<DataBoundViewHolder<*>>(), ItemTouchHelperAdapter {

    private val data = mutableListOf<Model>()
    private val callback = ItemTouchHelperCallback(this)
    private val touchHelper = ItemTouchHelper(callback)
    var touchListener : TouchHelper? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder, viewType)
        return viewHolder
    }

    protected abstract fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>, viewType: Int)

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        bind(holder.binding, data[position], position)
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: ViewDataBinding, item: Model, position: Int)

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = data[position].type

    fun updateDataSet(list: List<Model>){
        data.clearThenAdd(list)
        notifyDataSetChanged()
    }

    fun startDrag(viewHolder: RecyclerView.ViewHolder){
        touchHelper.startDrag(viewHolder)
    }

    override fun onItemMove(from: Int, to: Int) {
        if (from < to){
            for (position in from until to){
                data.swap(position , position + 1)
            }
        } else {
            for (position in from downTo to + 1){
                data.swap(position , position - 1)
            }
        }
        notifyItemMoved(from, to)
        touchListener?.onMove(from, to)
    }

    override fun onItemDismiss(position: Int) {
        data.removeAt(position)
        touchListener?.onDismiss(position)
    }

    abstract override val draggableViewType: Int
}

interface TouchHelper {
    fun onMove(from: Int, to: Int)
    fun onDismiss(position: Int)
}
package dev.olog.presentation.activity_preferences.categories

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import dev.olog.domain.entity.LibraryCategoryBehavior
import dev.olog.presentation.BR
import dev.olog.presentation.R
import dev.olog.presentation._base.list.DataBoundViewHolder
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperAdapter
import dev.olog.presentation.utils.recycler_view.ItemTouchHelperCallback
import dev.olog.shared.clearThenAdd
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.dialog_list_multi_choice_item.view.*
import javax.inject.Inject

class LibraryCategoriesFragmentAdapter @Inject constructor(

        val data: MutableList<LibraryCategoryBehavior>

) : RecyclerView.Adapter<DataBoundViewHolder<*>>(),
        ItemTouchHelperAdapter {

    private val callback = ItemTouchHelperCallback(this, false)
    val touchHelper = ItemTouchHelper(callback)

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = R.layout.dialog_list_multi_choice_item

    override fun onBindViewHolder(holder: DataBoundViewHolder<*>, position: Int) {
        holder.binding.setVariable(BR.item, data[position])
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder<*>){
        viewHolder.itemView.findViewById<View>(R.id.dragHandle).setOnTouchListener { _, event ->
            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper.startDrag(viewHolder)
                true
            } else false
        }
        viewHolder.itemView.findViewById<View>(R.id.checkBox).setOnClickListener {
            val item = data[viewHolder.adapterPosition]
            item.enabled = !item.enabled
            viewHolder.itemView.checkBox.isChecked = item.enabled
        }
    }

    fun updateDataSet(list: List<LibraryCategoryBehavior>){
        this.data.clearThenAdd(list)
        notifyDataSetChanged()
    }

    override fun onItemMove(from: Int, to: Int) {
        data.swap(from, to)
        data.forEachIndexed { index, item ->
            item.order = index
        }
        notifyItemMoved(from, to)
    }

    override fun onItemDismiss(position: Int) {
        throw IllegalStateException("operation not supported")
    }

    override val draggableViewType = R.layout.dialog_list_multi_choice_item


}
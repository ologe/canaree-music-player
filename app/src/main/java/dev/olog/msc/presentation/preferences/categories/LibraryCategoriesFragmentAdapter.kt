package dev.olog.msc.presentation.preferences.categories

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.presentation.base.adapter.DataBoundViewHolder
import dev.olog.msc.presentation.base.adapter.drag.TouchableAdapter
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.dialog_tab_category_item.view.*

class LibraryCategoriesFragmentAdapter (
        val data: MutableList<LibraryCategoryBehavior>

) : androidx.recyclerview.widget.RecyclerView.Adapter<DataBoundViewHolder>(), TouchableAdapter {

    var touchHelper: ItemTouchHelper? = null

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int = R.layout.dialog_tab_category_item

    override fun onBindViewHolder(holder: DataBoundViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, data[position])
        holder.binding.executePendingBindings()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, viewType, parent, false)
        val viewHolder = DataBoundViewHolder(binding)
        initViewHolderListeners(viewHolder)
        return viewHolder
    }

    private fun initViewHolderListeners(viewHolder: DataBoundViewHolder){
        viewHolder.itemView.findViewById<View>(R.id.dragHandle).setOnTouchListener { _, event ->
            if(event.actionMasked == MotionEvent.ACTION_DOWN) {
                touchHelper?.startDrag(viewHolder)
                true
            } else false
        }
        viewHolder.itemView.setOnClickListener {
            val item = data[viewHolder.adapterPosition]
            item.visible = !item.visible
            viewHolder.itemView.checkBox.isChecked = item.visible
        }
    }

    fun updateDataSet(list: List<LibraryCategoryBehavior>){
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onMoved(from: Int, to: Int) {
        data.swap(from, to)
        data.forEachIndexed { index, item -> item.order = index }
        notifyItemMoved(from, to)
    }

    override fun onSwipedLeft(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        throw IllegalStateException("operation not supported")
    }

    override fun onSwipedRight(position: Int) {
        throw IllegalStateException("operation not supported")
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean? {
        return viewType == R.layout.dialog_tab_category_item
    }

    override fun onClearView() {

    }
}
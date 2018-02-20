package dev.olog.msc.presentation.preferences.categories

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.domain.entity.LibraryCategoryBehavior
import dev.olog.msc.presentation.base.adp.DataBoundViewHolder
import dev.olog.msc.presentation.base.adp.TouchableAdapter
import dev.olog.msc.utils.k.extension.swap
import kotlinx.android.synthetic.main.dialog_tab_category_item.view.*

class LibraryCategoriesFragmentAdapter (
        val data: MutableList<LibraryCategoryBehavior>

) : RecyclerView.Adapter<DataBoundViewHolder>(), TouchableAdapter {

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

    override fun onSwiped(position: Int) {
        throw IllegalStateException("operation not supported")
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean? {
        return viewHolder.itemViewType == R.layout.dialog_tab_category_item
    }


}
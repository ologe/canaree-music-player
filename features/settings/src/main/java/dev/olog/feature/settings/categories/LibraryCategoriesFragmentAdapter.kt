package dev.olog.feature.settings.categories

import dev.olog.feature.presentation.base.adapter.DataBoundViewHolder
import dev.olog.feature.presentation.base.adapter.SimpleAdapter
import dev.olog.feature.presentation.base.adapter.setOnDragListener
import dev.olog.feature.presentation.base.adapter.drag.IDragListener
import dev.olog.feature.presentation.base.adapter.drag.TouchableAdapter
import dev.olog.feature.presentation.base.model.LibraryCategoryBehavior
import dev.olog.feature.settings.R
import dev.olog.shared.swap

internal class LibraryCategoriesFragmentAdapter(
    private val dragListener: IDragListener
) : SimpleAdapter<LibraryCategoryBehavior>(),
    TouchableAdapter {

    override fun getItemViewType(position: Int): Int = R.layout.item_library_categories

    override fun bind(holder: DataBoundViewHolder, item: LibraryCategoryBehavior, position: Int) {
        holder.itemView.apply {
//            checkBox.text = item.asString(context)
//            checkBox.isChecked = item.visible
        }
    }

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)

        viewHolder.itemView.setOnClickListener {
            val item = getItem(viewHolder.adapterPosition)
            item.visible = !item.visible
//            viewHolder.itemView.checkBox.isChecked = item.visible
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_library_categories
    }

    override fun onMoved(from: Int, to: Int) {
        backedList.forEachIndexed { index, item -> item.order = index }
        backedList.swap(from, to)
        notifyItemMoved(from, to)
    }
}
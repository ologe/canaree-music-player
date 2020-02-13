package dev.olog.presentation.prefs.categories

import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import dev.olog.presentation.base.adapter.setOnDragListener
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.item_library_categories.view.*

class LibraryCategoriesFragmentAdapter(
    private val dragListener: IDragListener
) : SimpleAdapter<LibraryCategoryBehavior>(),
    TouchableAdapter {

    override fun getItemViewType(position: Int): Int = R.layout.item_library_categories

    override fun bind(holder: DataBoundViewHolder, item: LibraryCategoryBehavior, position: Int) {
        holder.itemView.apply {
            checkBox.text = item.asString(context)
            checkBox.isChecked = item.visible
        }
    }

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)

        viewHolder.itemView.setOnClickListener {
            getItem(viewHolder.adapterPosition)?.let { item ->
                item.visible = !item.visible
                viewHolder.itemView.checkBox.isChecked = item.visible
            }
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_library_categories
    }

    override fun onMoved(from: Int, to: Int) {
        // TODO check
        currentList.forEachIndexed { index, item -> item.order = index }
        currentList.swap(from, to)
        notifyItemMoved(from, to)
    }
}
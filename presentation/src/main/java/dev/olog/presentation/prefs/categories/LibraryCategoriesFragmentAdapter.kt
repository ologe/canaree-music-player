package dev.olog.presentation.prefs.categories

import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.LayoutContainerViewHolder
import dev.olog.presentation.base.adapter.SimpleAdapter
import dev.olog.presentation.base.adapter.setOnDragListener
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.model.LibraryCategoryBehavior
import dev.olog.shared.autoDisposeJob
import dev.olog.shared.swap
import kotlinx.android.synthetic.main.item_library_categories.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryCategoriesFragmentAdapter (
    data: List<LibraryCategoryBehavior>,
    private val dragListener: IDragListener,
) : SimpleAdapter<LibraryCategoryBehavior>(data.toMutableList()),
    TouchableAdapter {

    private var job by autoDisposeJob()

    override fun getItemViewType(position: Int): Int = R.layout.item_library_categories

    override fun LayoutContainerViewHolder.bind(
        item: LibraryCategoryBehavior,
        position: Int
    ) = bindView {
        checkBox.text = item.asString(context)
        checkBox.isChecked = item.visible
    }

    override fun initViewHolderListeners(viewHolder: LayoutContainerViewHolder, viewType: Int) {
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)

        viewHolder.itemView.setOnClickListener {
            val item = getItem(viewHolder.adapterPosition)

            item.visible = !item.visible
            viewHolder.bindView {
                checkBox.isChecked = item.visible
            }
        }
    }

    override fun canInteractWithViewHolder(viewType: Int): Boolean {
        return viewType == R.layout.item_library_categories
    }

    override fun onMoved(from: Int, to: Int) {
        job = GlobalScope.launch {
            delay(200)
            dataSet.forEachIndexed { index, item -> item.order = index }
        }
        dataSet.swap(from, to)
        notifyItemMoved(from, to)
    }
}
package dev.olog.feature.settings.library

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.feature.settings.R
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.SimpleAdapter
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.feature.library.api.LibraryCategoryBehavior
import dev.olog.shared.extension.swap
import kotlinx.android.synthetic.main.item_library_categories.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LibraryCategoriesFragmentAdapter (
        data: List<LibraryCategoryBehavior>,
        private val dragListener: IDragListener
) : SimpleAdapter<LibraryCategoryBehavior>(data.toMutableList()),
    TouchableAdapter {

    private var job: Job? = null

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

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemViewType == R.layout.item_library_categories
    }

    override fun onMoved(from: Int, to: Int) {
        job?.cancel()
        job = GlobalScope.launch {
            delay(200)
            dataSet.forEachIndexed { index, item -> item.order = index }
        }

        dataSet.swap(from, to)
        notifyItemMoved(from, to)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
    }
}
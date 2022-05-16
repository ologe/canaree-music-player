package dev.olog.feature.detail.recently.added

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.feature.detail.R
import dev.olog.image.provider.BindingsAdapter
import dev.olog.platform.adapter.DataBoundViewHolder
import dev.olog.platform.adapter.ObservableAdapter
import dev.olog.platform.adapter.drag.IDragListener
import dev.olog.platform.adapter.drag.TouchableAdapter
import dev.olog.platform.adapter.elevateAlbumOnTouch
import dev.olog.platform.adapter.setOnClickListener
import dev.olog.platform.adapter.setOnDragListener
import dev.olog.platform.adapter.setOnLongClickListener
import dev.olog.ui.model.DiffCallbackDisplayableItem
import dev.olog.ui.model.DisplayableItem
import dev.olog.ui.model.DisplayableTrack
import kotlinx.android.synthetic.main.item_recently_added.view.*

class RecentlyAddedFragmentAdapter(
    private val onItemClick: (MediaId) -> Unit,
    private val onItemLongClick: (View, MediaId) -> Unit,
    private val onSwipeLeft: (MediaId) -> Unit,
    private val dragListener: IDragListener
) : ObservableAdapter<DisplayableItem>(DiffCallbackDisplayableItem), TouchableAdapter {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(this) { item, _, _ ->
            onItemClick(item.mediaId)
        }
        viewHolder.setOnLongClickListener(this) { item, _, _ ->
            onItemLongClick(viewHolder.itemView, item.mediaId)
        }
        viewHolder.setOnClickListener(R.id.more, this) { item, _, view ->
            onItemLongClick(view, item.mediaId)
        }
        viewHolder.elevateAlbumOnTouch()
        viewHolder.setOnDragListener(R.id.dragHandle, dragListener)
    }

    override fun bind(holder: DataBoundViewHolder, item: DisplayableItem, position: Int) {
        require(item is DisplayableTrack)

        holder.itemView.apply {
            BindingsAdapter.loadSongImage(holder.imageView!!, item.mediaId)
            firstText.text = item.title
            secondText.text = item.subtitle
            explicit.onItemChanged(item.title)
        }
    }

    override fun canInteractWithViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemViewType == R.layout.item_recently_added
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)!!
        onSwipeLeft(item.mediaId)
    }

    override fun contentViewFor(holder: RecyclerView.ViewHolder): View {
        return holder.itemView.content
    }
}